package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.minzheng.blog.config.QQConfigProperties;
import com.minzheng.blog.config.WeiboConfigProperties;
import com.minzheng.blog.constant.CommonConst;
import com.minzheng.blog.dao.RoleDao;
import com.minzheng.blog.dao.UserInfoDao;
import com.minzheng.blog.dao.UserRoleDao;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.UserInfo;
import com.minzheng.blog.entity.UserAuth;
import com.minzheng.blog.dao.UserAuthDao;
import com.minzheng.blog.entity.UserRole;
import com.minzheng.blog.enums.LoginTypeEnum;
import com.minzheng.blog.enums.RoleEnum;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.RedisService;
import com.minzheng.blog.service.UserAuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.utils.IpUtil;
import com.minzheng.blog.utils.UserUtil;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.PasswordVO;
import com.minzheng.blog.vo.UserVO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.minzheng.blog.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.minzheng.blog.constant.RedisPrefixConst.*;
import static com.minzheng.blog.constant.ThirdLoginConst.*;
import static com.minzheng.blog.utils.CommonUtil.checkEmail;
import static com.minzheng.blog.utils.UserUtil.convertLoginUser;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthDao, UserAuth> implements UserAuthService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private HttpServletRequest request;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private QQConfigProperties qqConfigProperties;
    @Autowired
    private WeiboConfigProperties weiboConfigProperties;

    @Override
    public void sendCode(String username) {
        // 校验账号是否合法
        if (!checkEmail(username)) {
            throw new ServeException("请输入正确邮箱");
        }
        // 生成六位随机验证码发送
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        // 发送验证码
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .content("您的验证码为 " + code.toString() + " 有效期15分钟，请不要告诉他人哦！")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        // 将验证码存入redis，设置过期时间为15分钟
        redisService.set(CODE_KEY + username, code, CODE_EXPIRE_TIME);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUser(UserVO user) {
        // 校验账号是否合法
        if (checkUser(user)) {
            throw new ServeException("邮箱已被注册！");
        }
        // 新增用户信息
        UserInfo userInfo = UserInfo.builder()
                .email(user.getUsername())
                .nickname(CommonConst.DEFAULT_NICKNAME)
                .avatar(CommonConst.DEFAULT_AVATAR)
                .createTime(new Date())
                .build();
        userInfoDao.insert(userInfo);
        // 绑定用户角色
        saveUserRole(userInfo);
        // 新增用户账号
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .createTime(new Date())
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthDao.insert(userAuth);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassword(UserVO user) {
        // 校验账号是否合法
        if (!checkUser(user)) {
            throw new ServeException("邮箱尚未注册！");
        }
        // 根据用户名修改密码
        userAuthDao.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUsername, user.getUsername()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // 查询旧密码是否正确
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, UserUtil.getLoginUser().getId()));
        // 正确则修改密码，错误则提示不正确
        if (Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(), user.getPassword())) {
            UserAuth userAuth = UserAuth.builder()
                    .id(UserUtil.getLoginUser().getId())
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
                    .build();
            userAuthDao.updateById(userAuth);
        } else {
            throw new ServeException("旧密码不正确");
        }
    }

    @Override
    public PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        // 转换页码
        condition.setCurrent((condition.getCurrent() - 1) * condition.getSize());
        // 获取后台用户数量
        Integer count = userAuthDao.countUser(condition);
        if (count == 0) {
            return new PageDTO<>();
        }
        // 获取后台用户列表
        List<UserBackDTO> userBackDTOList = userAuthDao.listUsers(condition);
        return new PageDTO<>(userBackDTOList, count);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoDTO qqLogin(String openId, String accessToken) {
        // 创建登录信息
        UserInfoDTO userInfoDTO;
        // 校验该第三方账户信息是否存在
        UserAuth user = getUserAuth(openId, LoginTypeEnum.QQ.getType());
        if (Objects.nonNull(user) && Objects.nonNull(user.getUserInfoId())) {
            // 存在则返回数据库中的用户信息登录封装
            userInfoDTO = getUserInfoDTO(user);
        } else {
            // 不存在通过openId和accessToken获取QQ用户信息，并创建用户
            Map<String, String> formData = new HashMap<>(3);
            // 定义请求参数
            formData.put(QQ_OPEN_ID, openId);
            formData.put(ACCESS_TOKEN, accessToken);
            formData.put(OAUTH_CONSUMER_KEY, qqConfigProperties.getAppId());
            // 获取QQ返回的用户信息
            QQUserInfoDTO qqUserInfoDTO = JSON.parseObject(restTemplate.getForObject(qqConfigProperties.getUserInfoUrl(), String.class, formData), QQUserInfoDTO.class);
            // 获取ip地址
            String ipAddr = IpUtil.getIpAddr(request);
            String ipSource = IpUtil.getIpSource(ipAddr);
            // 将用户账号和信息存入数据库
            UserInfo userInfo = convertUserInfo(Objects.requireNonNull(qqUserInfoDTO).getNickname(), qqUserInfoDTO.getFigureurl_qq_1());
            userInfoDao.insert(userInfo);
            UserAuth userAuth = convertUserAuth(userInfo.getId(), openId, accessToken, ipAddr, ipSource, LoginTypeEnum.QQ.getType());
            userAuthDao.insert(userAuth);
            // 绑定角色
            saveUserRole(userInfo);
            // 封装登录信息
            userInfoDTO = convertLoginUser(userAuth, userInfo, Lists.newArrayList(RoleEnum.USER.getLabel()), null, null, request);
        }
        // 将登录信息放入springSecurity管理
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfoDTO, null, userInfoDTO.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return userInfoDTO;
    }

    /**
     * 绑定用户角色
     *
     * @param userInfo 用户信息
     */
    private void saveUserRole(UserInfo userInfo) {
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleDao.insert(userRole);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public UserInfoDTO weiBoLogin(String code) {
        // 创建登录信息
        UserInfoDTO userInfoDTO;
        // 用code换取accessToken和uid
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        // 定义请求参数
        formData.add(CLIENT_ID, weiboConfigProperties.getAppId());
        formData.add(CLIENT_SECRET, weiboConfigProperties.getAppSecret());
        formData.add(GRANT_TYPE, weiboConfigProperties.getGrantType());
        formData.add(REDIRECT_URI, weiboConfigProperties.getRedirectUrl());
        formData.add(CODE, code);
        // 构建参数体
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, null);
        // 获取accessToken和uid
        WeiboTokenDTO weiboTokenDTO = restTemplate.exchange(weiboConfigProperties.getAccessTokenUrl(), HttpMethod.POST, requestEntity, WeiboTokenDTO.class).getBody();
        String uid = Objects.requireNonNull(weiboTokenDTO).getUid();
        String accessToken = weiboTokenDTO.getAccess_token();
        // 校验该第三方账户信息是否存在
        UserAuth user = getUserAuth(uid, LoginTypeEnum.WEIBO.getType());
        if (Objects.nonNull(user) && Objects.nonNull(user.getUserInfoId())) {
            // 存在则返回数据库中的用户信息封装
            userInfoDTO = getUserInfoDTO(user);
        } else {
            // 不存在则用accessToken和uid换取微博用户信息，并创建用户
            Map<String, String> data = new HashMap<>(2);
            // 定义请求参数
            data.put(UID, uid);
            data.put(ACCESS_TOKEN, accessToken);
            // 获取微博用户信息
            WeiboUserInfoDTO weiboUserInfoDTO = restTemplate.getForObject(weiboConfigProperties.getUserInfoUrl(), WeiboUserInfoDTO.class, data);
            // 获取ip地址
            String ipAddr = IpUtil.getIpAddr(request);
            String ipSource = IpUtil.getIpSource(ipAddr);
            // 将账号和信息存入数据库
            UserInfo userInfo = convertUserInfo(Objects.requireNonNull(weiboUserInfoDTO).getScreen_name(), weiboUserInfoDTO.getAvatar_hd());
            userInfoDao.insert(userInfo);
            UserAuth userAuth = convertUserAuth(userInfo.getId(), uid, accessToken, ipAddr, ipSource, LoginTypeEnum.WEIBO.getType());
            userAuthDao.insert(userAuth);
            // 绑定角色
            saveUserRole(userInfo);
            // 封装登录信息
            userInfoDTO = convertLoginUser(userAuth, userInfo, Lists.newArrayList(RoleEnum.USER.getLabel()), null, null, request);
        }
        // 将登录信息放入springSecurity管理
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfoDTO, null, userInfoDTO.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return userInfoDTO;
    }

    /**
     * 封装用户信息
     *
     * @param nickname 昵称
     * @param avatar   头像
     * @return 用户信息
     */
    private UserInfo convertUserInfo(String nickname, String avatar) {
        return UserInfo.builder()
                .nickname(nickname)
                .avatar(avatar)
                .createTime(new Date())
                .build();
    }

    /**
     * 封装用户账号
     *
     * @param userInfoId  用户信息id
     * @param uid         唯一Id标识
     * @param accessToken 登录凭证
     * @param ipAddr      ip地址
     * @param ipSource    ip来源
     * @param loginType   登录方式
     * @return 用户账号
     */
    private UserAuth convertUserAuth(Integer userInfoId, String uid, String accessToken, String ipAddr, String ipSource, Integer loginType) {
        return UserAuth.builder()
                .userInfoId(userInfoId)
                .username(uid)
                .password(accessToken)
                .loginType(loginType)
                .ipAddr(ipAddr)
                .ipSource(ipSource)
                .createTime(new Date())
                .lastLoginTime(new Date())
                .build();
    }

    /**
     * 获取本地第三方登录信息
     *
     * @param user 用户对象
     * @return 用户登录信息
     */
    private UserInfoDTO getUserInfoDTO(UserAuth user) {
        // 更新登录时间，ip
        String ipAddr = IpUtil.getIpAddr(request);
        String ipSource = IpUtil.getIpSource(ipAddr);
        userAuthDao.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getLastLoginTime, new Date())
                .set(UserAuth::getIpAddr, ipAddr)
                .set(UserAuth::getIpSource, ipSource)
                .eq(UserAuth::getId, user.getId()));
        // 查询账号对应的信息
        UserInfo userInfo = userInfoDao.selectOne(new LambdaQueryWrapper<UserInfo>()
                .select(UserInfo::getId, UserInfo::getEmail, UserInfo::getNickname, UserInfo::getAvatar, UserInfo::getIntro, UserInfo::getWebSite, UserInfo::getIsDisable)
                .eq(UserInfo::getId, user.getUserInfoId()));
        // 查询账号点赞信息
        Set<Integer> articleLikeSet = (Set<Integer>) redisService.hGet(ARTICLE_USER_LIKE, userInfo.getId().toString());
        Set<Integer> commentLikeSet = (Set<Integer>) redisService.hGet(COMMENT_USER_LIKE, userInfo.getId().toString());
        // 查询账号角色
        List<String> roleList = roleDao.listRolesByUserInfoId(userInfo.getId());
        // 封装信息
        return convertLoginUser(user, userInfo, roleList, articleLikeSet, commentLikeSet, request);
    }


    /**
     * 检测第三方账号是否注册
     *
     * @param openId    第三方唯一id
     * @param loginType 登录方式
     * @return 用户账号信息
     */
    private UserAuth getUserAuth(String openId, Integer loginType) {
        // 查询账号信息
        return userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getId, UserAuth::getUserInfoId, UserAuth::getLoginType)
                .eq(UserAuth::getUsername, openId)
                .eq(UserAuth::getLoginType, loginType));
    }


    /**
     * 校验用户数据是否合法
     *
     * @param user 用户数据
     * @return 合法状态
     */
    private Boolean checkUser(UserVO user) {
        if (!user.getCode().equals(redisService.get(CODE_KEY + user.getUsername()))) {
            throw new ServeException("验证码错误！");
        }
        //查询用户名是否存在
        UserAuth userAuth = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername).eq(UserAuth::getUsername, user.getUsername()));
        return Objects.nonNull(userAuth);
    }

}
