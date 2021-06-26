import axios from "axios";
//获取歌词
export const getWords = id => {
  return axios.get(`https://autumnfish.cn/lyric?id=${id}`);
};
//获取歌曲详情
export const getMusicInfo = id => {
  return axios.get(`https://autumnfish.cn/song/detail?ids=${id}`);
};
//获取歌曲url
export const getMusicUrl = id => {
  return axios.get(`https://autumnfish.cn/song/url?id=${id}`);
};
//获取热门歌曲
export const getHotMusic = id => {
  return axios.get(`https://autumnfish.cn/playlist/detail?id=${id}`);
};
//获取搜索建议
export const getSearchSuggest = key => {
  return axios.get(`https://autumnfish.cn/search/suggest?keywords=${key}`);
};
//获取歌曲热门评论
export const getHotTalk = id => {
  return axios.get(`https://autumnfish.cn/comment/music?id=${id}&limit=3`);
};
