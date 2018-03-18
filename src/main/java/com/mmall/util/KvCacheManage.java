package com.mmall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class KvCacheManage {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static final Log log = LogFactory.getLog(KvCacheManage.class);

	public void setObject(final String key, Object object) {

		final String data = JSON.toJSONString(object);

		try {
			redisTemplate.execute(new RedisCallback<Integer>() {

				@Override
				public Integer doInRedis(RedisConnection connection) throws DataAccessException {

					try {
						connection.set(key.getBytes(), data.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public <T> T getObject(final String key, Class<T> clazz) {

		String data;
		try {
			data = redisTemplate.execute(new RedisCallback<String>() {

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bb = connection.get(key.getBytes());

					if (bb != null) try {
						return new String(bb, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return null;
				}
			});

			if (data != null) {
				return JSON.parseObject(data, clazz);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

		return null;
	}

	public <T> T getObject(final String key, TypeReference<T> typeReference) {

		String data;
		try {
			data = redisTemplate.execute(new RedisCallback<String>() {

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bb = connection.get(key.getBytes());

					if (bb != null) try {
						return new String(bb, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return null;
				}
			});

			if (data != null) {
				return JSON.parseObject(data, typeReference);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public void setObject(final String key, Object object, final long expire) {
		final String data = JSON.toJSONString(object);

		try {
			redisTemplate.execute(new RedisCallback<Integer>() {

				@Override
				public Integer doInRedis(RedisConnection connection) throws DataAccessException {
					try {
						connection.setEx(key.getBytes(), expire, data.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public void setString(final String key, final String object) {

		try {
			redisTemplate.execute(new RedisCallback<Integer>() {

				@Override
				public Integer doInRedis(RedisConnection connection) throws DataAccessException {

					try {
						connection.set(key.getBytes(), object.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void setLong(final String key, final long object) {

		try {
			redisTemplate.execute(new RedisCallback<Integer>() {

				@Override
				public Integer doInRedis(RedisConnection connection) throws DataAccessException {

					connection.set(key.getBytes(), (object + "").getBytes());
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getString(final String key) {
		try {
			String data = redisTemplate.execute(new RedisCallback<String>() {

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bb = connection.get(key.getBytes());

					if (bb != null) try {
						return new String(bb, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return null;
				}
			});
			return data;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public long inc(final String key) {

		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.incr(key.getBytes());
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}

	}

	public long inc(final String key, final long value) {

		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.incrBy(key.getBytes(), value);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}

	}

	public long getLong(final String key) {
		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] b = connection.get(key.getBytes());

					if (b == null) {
						return -1l;
					}

					return Long.parseLong(new String(b));
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}
	}

	public boolean expire(final String key, final long seconds) {
		try {
			return redisTemplate.execute(new RedisCallback<Boolean>() {

				@Override
				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.expire(key.getBytes(), seconds);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	public long decr(final String key) {

		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.decr(key.getBytes());
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}
	}

	public long decr(final String key, final long value) {

		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.decrBy(key.getBytes(), value);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}
	}

	public void del(final String key) {
		try {
			redisTemplate.execute(new RedisCallback<Integer>() {

				@Override
				public Integer doInRedis(RedisConnection connection) throws DataAccessException {
					connection.del(key.getBytes());
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public void del(final List<String> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		try {
			redisTemplate.execute(new RedisCallback<Integer>() {

				@Override
				public Integer doInRedis(RedisConnection connection) throws DataAccessException {
					byte[][] keys = new byte[list.size()][];
					for (int i = 0; i < list.size(); i++) {
						keys[i] = list.get(i).getBytes();
					}
					connection.del(keys);
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * 批量获取
	 *
	 * @param list
	 * @return
	 */
	public Map<String, Long> batchGetLong(final List<String> list) {
		if (list == null || list.size() == 0) return null;
		try {
			return redisTemplate.execute(new RedisCallback<Map<String, Long>>() {

				@Override
				public Map<String, Long> doInRedis(RedisConnection connection) throws DataAccessException {

					byte[][] keys = new byte[list.size()][];
					for (int i = 0; i < list.size(); i++) {
						keys[i] = list.get(i).getBytes();
					}

					List<byte[]> datalist = connection.mGet(keys);

					Map<String, Long> map = new HashMap<String, Long>();

					for (int i = 0; i < datalist.size(); i++) {
						byte[] data = datalist.get(i);
						byte[] key = keys[i];
						if (data != null) {
							try {
								map.put(new String(key), Long.parseLong(new String(data)));
							} catch (NumberFormatException e) {
								map.put(new String(key), null);
							}
						} else map.put(new String(key), null);
					}

					return map;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}

	public boolean exists(final String key) {

		try {
			return redisTemplate.execute(new RedisCallback<Boolean>() {

				@Override
				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.exists(key.getBytes());
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	public Long sadd(final String key, Object... objects) {
		final byte[][] valuses = new byte[objects.length][];
		for (int i = 0; i < objects.length; i++) {
			String data = JSON.toJSONString(objects[i]);
			valuses[i] = data.getBytes();
		}
		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.sAdd(key.getBytes(), valuses);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}
	}

	public <T> List<T> getSmembers(final String key, final Class<T> clazz) {
		List<T> data = null;
		try {
			data = redisTemplate.execute(new RedisCallback<List<T>>() {

				@Override
				public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
					Set<byte[]> bb = connection.sMembers(key.getBytes());

					List<T> list = new ArrayList<>(bb.size());

					String tempStr = null;
					for (byte[] b : bb) {
						if (b != null) try {
							tempStr = new String(b, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							log.error(e.getMessage(), e);
						}

						if (tempStr != null) {
							list.add(JSON.parseObject(tempStr, clazz));
						}
					}
					return list;
				}
			});

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return data;
	}

	public <T> T sPop(final String key, Class<T> clazz) {
		String data;
		try {
			data = redisTemplate.execute(new RedisCallback<String>() {

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bb = connection.sPop(key.getBytes());

					if (bb != null) try {
						return new String(bb, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage(), e);
					}
					return null;
				}
			});

			if (data != null) {
				return JSON.parseObject(data, clazz);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}

		return null;
	}

	public <T> Long sRem(final String key, List<T> objects) {
		final byte[][] valuses = new byte[objects.size()][];
		for (int i = 0; i < objects.size(); i++) {
			String data = JSON.toJSONString(objects.get(i));
			valuses[i] = data.getBytes();
		}
		try {
			return redisTemplate.execute(new RedisCallback<Long>() {

				@Override
				public Long doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.sRem(key.getBytes(), valuses);
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return -1L;
		}
	}

	public Boolean setIfNotExists(final String key, final long expire) {
		try {
			Boolean ret = redisTemplate.execute(new RedisCallback<Boolean>() {

				@Override
				public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] keybytes = key.getBytes();
					Boolean suc = connection.setNX(keybytes,
							String.valueOf(System.currentTimeMillis() + expire * 1000).getBytes());
					// 每次执行后设置超时时间，防止一直存留在redis
					// 要求redis自动失效时间大于expire，保证不会自动失效
					// connection.expire(keybytes, expire * 2);
					if (suc) {
						return true;
					}
					byte[] oldValueBytes = connection.get(keybytes);
					if (oldValueBytes != null) {
						long oldValue = Long.parseLong(new String(oldValueBytes));
						if (System.currentTimeMillis() > oldValue) {// 已超时
							byte[] oldValueAgain = connection.getSet(keybytes,
									String.valueOf(System.currentTimeMillis() + expire * 1000).getBytes());
							if (oldValueAgain != null && Long.parseLong(new String(oldValueAgain)) == oldValue) {
								return true;
							}
						}
					}
					return false;
				}
			});
			return ret;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

}
