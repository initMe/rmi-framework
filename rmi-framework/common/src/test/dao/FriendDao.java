package test.dao;

import java.util.List;

import com.dao.jdbc.JdbcDao;
import com.exception.DataBaseException;
import com.exception.Error;

/** 用户表痕迹dao */
public class FriendDao extends JdbcDao<t_friend> {
	/** 单例对象 */
	private static FriendDao fd = null;
	/** 单例本类 */
	private FriendDao() {}
	/** 获取单例 */
	public static synchronized FriendDao getInstance() {
		if(fd == null) {
			fd = new FriendDao();
		}
		return fd;
	}
	
	/**
	 * 获取好友列表
	 * @param selfId	自己的userId
	 * @return	好友信息
	 */
	public List<t_user> getFriendList(Integer selfId) {
		String sql = "" +
				"select * from t_user " +
				"where id in ( " +
				"	select friendId from t_friend selfId=? " +
				") or id in (" +
				"	select selfId from t_friend friendId=? " +
				") ";
		try {
			return (List<t_user>) getSqlManager().query(t_user.class, null, null, sql, selfId, selfId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataBaseException(Error.database_error, e);
		}
	}
	
	public static void main(String[] args) {
		FriendDao.getInstance().queryAll();
	}
}
