package com.suke.czx.controller;

import com.suke.czx.utils.GenUtils;
import com.suke.czx.utils.R;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 * 
 * @author czx
 * @email object_czx@163.com
 * @date 2016年12月19日 下午9:12:58
 */
@RestController
@RequestMapping("/sys/generator")
public class SysGeneratorController {

	private Map<String,Object> map = null;

	/**
	 * 生成代码
	 */
	@RequestMapping("/setParam")
	public R setParam(@RequestBody Map<String, Object> params) throws IOException{
		map = params;
		return R.ok();
	}

	/**
	 * 生成代码
	 */
	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException{
		byte[] data = generatorCode(map);
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"x-android.zip\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream; charset=UTF-8");

		IOUtils.write(data, response.getOutputStream());
	}

	public byte[] generatorCode(Map<String, Object> params) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);
		GenUtils.generatorCode(params, zip);
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}
}
