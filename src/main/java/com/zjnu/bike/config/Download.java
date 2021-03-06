package com.zjnu.bike.config;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.zjnu.bike.gridfs.GridFSConfig;
import com.zjnu.bike.security.SessionSecurity;

import lombok.extern.slf4j.Slf4j;

/**
 * 下载
 * @author ChenTao
 * @date 2015年8月12日下午11:52:40
 */
@Controller
@Slf4j
public class Download {

	@Autowired
	public MVCConfig config;

	@Autowired
	private SessionSecurity sessionSecurity;

	@Autowired
	private GridFSConfig gridFSConfig;

	/**
	 * 下载主方法
	 * 通过GridFS把文件保存到数据库
	 * @author ChenTao
	 * @date 2015年8月12日下午11:52:49
	 */
	@RequestMapping("/download/{id:.*}")
	public void download(@PathVariable("id") String id, HttpServletResponse response, HttpSession session, ModelMap map) throws Exception {
		log.debug("{}", id);
		if (StringUtils.isBlank(id)) {
			throw new Exception("id为空");
		}
		if (!this.sessionSecurity.getMethod(session)) {
			throw new Exception("权限错误");
		}
		Mongo mongo = null;
		GridFSDBFile gfsFile = null;
		try {
			mongo = gridFSConfig.mongo();
			DB db = mongo.getDB(gridFSConfig.getDatabase());
			GridFS gridFS = new GridFS(db);
			gfsFile = gridFS.findOne(new ObjectId(id));
			if (gfsFile == null) {
				throw new Exception("文件不存在");
			}
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + new String(gfsFile.getFilename().getBytes("utf-8"), "ISO-8859-1"));
			ServletOutputStream out = response.getOutputStream();
			gfsFile.writeTo(out);
		} catch (Exception e) {
			log.error("{}", e);
		} finally {
			mongo.close();
		}
	}

	/**
	 * 下载主方法
	 * 直接保存文件
	 * @author ChenTao
	 * @date 2015年8月12日下午11:52:49
	 */
	//@RequestMapping("/download/{id:.*}")
	/*public void downloadold(@PathVariable("id") String id, HttpServletResponse response, HttpSession session, ModelMap map) throws Exception {
		log.debug("{}", id);
		if (!this.sessionSecurity.getMethod(session)) {
			throw new Exception("权限错误");
		}
		FileInfo fileInfo = this.fileInfoRepository.findOne(id);
		if (fileInfo == null) {
			throw new Exception("文件不存在");
		}
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileInfo.getFilename().getBytes("utf-8"), "ISO-8859-1"));
		ServletOutputStream out = response.getOutputStream();
		Files.copy(Paths.get(config.getUploadPath() + fileInfo.getFileUrl(), fileInfo.getId().toString()), out);
	}*/

	/**
	 * 下载主方法
	 * 不推荐使用
	 * @author ChenTao
	 * @date 2015年8月12日下午11:52:49
	 */
	//@RequestMapping("/download/{name:.*}")
	/*public void downloadold(@PathVariable("name") String name, HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream; charset=utf-8");
		ServletOutputStream out = response.getOutputStream();
		Files.copy(Paths.get(config.getUploadPath(), name), out);
	}*/

	/**
	 * 测试
	 * @author ChenTao
	 * @date 2015年8月13日上午10:37:55
	 */
	//@RequestMapping("/downloadcheck")
	/*public void downloadcheck(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + new String("测试图片.jpg".getBytes("utf-8"), "ISO-8859-1"));
		ServletOutputStream out = response.getOutputStream();
		Files.copy(Paths.get(config.getUploadPath(), "测试图片.jpg"), out);
	}*/
}
