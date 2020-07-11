package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minzheng.blog.constant.LoginConst;
import com.minzheng.blog.dao.UserInfoDao;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.dto.UserBackDTO;
import com.minzheng.blog.dto.UserInfoDTO;
import com.minzheng.blog.entity.UserInfo;
import com.minzheng.blog.entity.UserAuth;
import com.minzheng.blog.dao.UserAuthDao;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.UserAuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.utils.IpUtil;
import com.minzheng.blog.utils.UserUtil;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.PasswordVO;
import com.minzheng.blog.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@SuppressWarnings("all")
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthDao, UserAuth> implements UserAuthService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpServletRequest request;

    /**
     * 过期时间
     */
    private static final long EXPIRE_TIME = 15 * 60 * 1000;

    /**
     * qq appId
     */
    @Value("${qq.app-id}")
    private String QQ_APP_ID;

    /**
     * qq获取用户信息接口地址
     */
    @Value("${qq.user-info-url}")
    private String QQ_USER_INFO_URL;

    /**
     * 微博appId
     */
    @Value("${weibo.app-id}")
    private String WEIBO_APP_ID;

    /**
     * 微博appSecret
     */
    @Value("${weibo.app-secret}")
    private String WEIBO_APP_SECRET;

    /**
     * 微博授权方式
     */
    @Value("${weibo.grant-type}")
    private String WEIBO_GRANT_TYPE;

    /**
     * 微博回调地址
     */
    @Value("${weibo.redirect-url}")
    private String WEIBO_REDIRECT_URI;

    /**
     * 微博获取token和openId接口地址
     */
    @Value("${weibo.access-token-url}")
    private String WEIBO_ACCESCC_TOKEN_URI;

    /**
     * 微博获取用户信息接口地址
     */
    @Value("${weibo.user-info-url}")
    private String WEIBO_USER_INFO_URI;

    @Override
    public void sendCode(String username) {
        if (!checkEmail(username)) {
            throw new ServeException("请输入正确邮箱");
        }
        //生成六位随机验证码
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1192176811@qq.com");
        message.setTo(username);
        message.setSubject("验证码");
        message.setText("您的验证码为 " + code.toString() + " 有效期15分钟，请不要告诉他人哦！");
        javaMailSender.send(message);
        //将验证码存入redis，设置过期时间为15分钟
        redisTemplate.boundValueOps("code_" + username).set(code);
        redisTemplate.expire("code_" + username, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void saveUser(UserVO user) {
        if (checkUser(user)) {
            throw new ServeException("邮箱已被注册！");
        }
        //新增用户信息
        UserInfo userInfo = new UserInfo();
        userInfoDao.insert(userInfo);
        //新增用户账号
        userAuthDao.insert(new UserAuth(userInfo.getId(), user.getUsername(), BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()), LoginConst.EMAIL));
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void updatePassword(UserVO user) {
        if (!checkUser(user)) {
            throw new ServeException("邮箱尚未注册！");
        }
        UpdateWrapper<UserAuth> updateWrapper = new UpdateWrapper();
        updateWrapper.set("password", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())).eq("username", user.getUsername());
        userAuthDao.update(new UserAuth(), updateWrapper);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        //查询旧密码是否正确
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("password").eq("id", UserUtil.getLoginUser().getId()).eq("password", passwordVO.getOldPassword());
        if (userAuthDao.selectOne(queryWrapper) != null) {
            userAuthDao.updateById(new UserAuth(passwordVO.getNewPassword()));
        }else {
            throw new ServeException("旧密码不正确");
        }
    }

    @Override
    public PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        //转换页码
        condition.setCurrent((condition.getCurrent() - 1) * condition.getSize());
        //获取后台用户数量
        Integer count = userAuthDao.countUser(condition);
        if (count == 0) {
            return new PageDTO<UserBackDTO>();
        }
        //获取后台用户列表
        List<UserBackDTO> userBackDTOList = userAuthDao.listUsers(condition);
        return new PageDTO<UserBackDTO>(userBackDTOList, count);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public UserInfoDTO qqLogin(String openId, String accessToken) {
        //创建登录信息
        UserInfoDTO userInfoDTO = null;
        //校验该第三方账户信息是否存在
        UserAuth user = getUserAuth(openId, LoginConst.QQ);
        if (user != null && user.getUserInfoId() != null) {
            //存在则返回数据库中的用户信息登录封装
            userInfoDTO = getUserInfoDTO(user);
        } else {
            //不存在通过openId和accessToken获取QQ用户信息，并创建用户
            Map<String, String> formData = new HashMap<>(16);
            //定义请求参数
            formData.put("openid", openId);
            formData.put("access_token", accessToken);
            formData.put("oauth_consumer_key", QQ_APP_ID);
            //获取QQ返回的用户信息
            String result = restTemplate.getForObject(QQ_USER_INFO_URL, String.class, formData);
            Map<String, String> userInfoMap = JSON.parseObject(result, Map.class);
            //获取ip地址
            String ipAddr = IpUtil.getIpAddr(request);
            String ipSource = IpUtil.getIpSource(ipAddr);
            //将账号和信息存入数据库(登录ip地址来源)
            UserInfo userInfo = new UserInfo(userInfoMap.get("nickname"), userInfoMap.get("figureurl_qq_1"));
            userInfoDao.insert(userInfo);
            UserAuth userAuth = new UserAuth(userInfo.getId(), openId, accessToken, LoginConst.QQ, ipAddr, ipSource);
            userAuthDao.insert(userAuth);
            //封装登录信息
            userInfoDTO = new UserInfoDTO(userAuth.getId(), userInfo, null, null);
        }
        //将登录信息放入springSecurity管理
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(JSON.toJSONString(userInfoDTO), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return userInfoDTO;
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public UserInfoDTO weiboLogin(String code) {
        //创建登录信息
        UserInfoDTO userInfoDTO = null;
        //用code换取accessToken和uid
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //定义请求参数
        formData.add("client_id", WEIBO_APP_ID);
        formData.add("client_secret", WEIBO_APP_SECRET);
        formData.add("grant_type", WEIBO_GRANT_TYPE);
        formData.add("redirect_uri", WEIBO_REDIRECT_URI);
        formData.add("code", code);
        //构建参数体
        HttpEntity<MultiValueMap> requestentity = new HttpEntity<MultiValueMap>(formData, null);
        //获取accessToken和uid
        Map<String, String> result = restTemplate.exchange(WEIBO_ACCESCC_TOKEN_URI, HttpMethod.POST, requestentity, Map.class).getBody();
        String uid = result.get("uid");
        String accessToken = result.get("access_token");
        //校验该第三方账户信息是否存在
        UserAuth user = getUserAuth(uid, LoginConst.WEIBO);
        if (user != null && user.getUserInfoId() != null) {
            //存在则返回数据库中的用户信息封装
            userInfoDTO = getUserInfoDTO(user);
        } else {
            //不存在则用accessToken和uid换取微博用户信息，并创建用户
            Map<String, String> data = new HashMap<>(16);
            //定义请求参数
            data.put("uid", uid);
            data.put("access_token", accessToken);
            //获取微博用户信息
            Map<String, String> userInfoMap = restTemplate.getForObject(WEIBO_USER_INFO_URI, Map.class, data);
            //获取ip地址
            String ipAddr = IpUtil.getIpAddr(request);
            String ipSource = IpUtil.getIpSource(ipAddr);
            //将账号和信息存入数据库(登录ip地址来源)
            UserInfo userInfo = new UserInfo(userInfoMap.get("screen_name"), userInfoMap.get("profile_image_url"));
            userInfoDao.insert(userInfo);
            UserAuth userAuth = new UserAuth(userInfo.getId(), uid, accessToken, LoginConst.WEIBO, ipAddr, ipSource);
            userAuthDao.insert(userAuth);
            //封装登录信息
            userInfoDTO = new UserInfoDTO(userAuth.getId(), userInfo, null, null);
        }
        //将登录信息放入springSecurity管理
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(JSON.toJSONString(userInfoDTO), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return userInfoDTO;
    }

    /**
     * 获取本地第三方登录信息
     *
     * @param openId
     * @param userInfoId
     * @return
     */
    private UserInfoDTO getUserInfoDTO(UserAuth user) {
        //更新登录时间，ip
        UpdateWrapper<UserAuth> updateWrapper = new UpdateWrapper<>();
        String ipAddr = IpUtil.getIpAddr(request);
        String ipSource = IpUtil.getIpSource(ipAddr);
        updateWrapper.set("last_login_time", new Date()).set("ip_addr", ipAddr).set("ip_source", ipSource).eq("id", user.getId());
        userAuthDao.update(new UserAuth(), updateWrapper);
        //查询账号对应的信息
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "user_role", "nickname", "avatar", "intro", "web_site", "is_silence").eq("id", user.getUserInfoId());
        UserInfo userInfo = userInfoDao.selectOne(queryWrapper);
        //查询账号点赞信息
        Set articleLikeSet = (Set) redisTemplate.boundHashOps("article_user_like").get(userInfo.getId().toString());
        Set commentLikeSet = (Set) redisTemplate.boundHashOps("comment_user_like").get(userInfo.getId().toString());
        return new UserInfoDTO(user.getId(), userInfo, articleLikeSet, commentLikeSet);
    }

    /**
     * 检测第三方账号是否注册
     *
     * @return 用户信息id
     */
    private UserAuth getUserAuth(String openId, Integer loginType) {
        QueryWrapper<UserAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "user_info_id").eq("username", openId).eq("login_type", loginType);
        return userAuthDao.selectOne(queryWrapper);
    }

    /**
     * 检测邮箱是否合法
     *
     * @param username 用户名
     * @return 合法状态
     */
    private boolean checkEmail(String username) {
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式 编译正则表达式
        Pattern p = Pattern.compile(RULE_EMAIL);
        //正则表达式的匹配器
        Matcher m = p.matcher(username);
        //进行正则匹配
        return m.matches();
    }

    /**
     * 校验用户数据是否合法
     *
     * @param user 用户数据
     */
    private Boolean checkUser(UserVO user) {
        if (!user.getCode().equals(redisTemplate.boundValueOps("code_" + user.getUsername()).get())) {
            throw new ServeException("验证码错误！");
        }
        //查询用户名是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("username").eq("username", user.getUsername());
        //存在返回true
        if (userAuthDao.selectOne(queryWrapper) != null) {
            return true;
        }
        //不存在返回false
        return false;
    }

}
