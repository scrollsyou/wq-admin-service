package com.yywh.Controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yywh.annotation.NotLogin;

@RequestMapping("/download")
@Controller
public class DownloadFileController {

	private Logger logs = LoggerFactory.getLogger(DownloadFileController.class);
	
	@NotLogin
    @RequestMapping(value = "/media")  
    public ResponseEntity<InputStreamResource> downloadFile(String fileName)  
            throws IOException {
        String filePath = "templateMusic.mp3";
        InputStream file = HttpServletRequest.class.getClassLoader().getResourceAsStream(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName+".mp3"));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
  
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.available())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file));
    }  
	@NotLogin
	@RequestMapping("/mp3")
	@ResponseBody
	public String addLog(String fileName, HttpServletRequest request, HttpServletResponse response){
		if(fileName == null || "".equals(fileName)) {
			fileName = "Music";
		}
        String filePath = "templateMusic.mp3";
        InputStream inputStream = DownloadFileController.class.getClassLoader().getResourceAsStream(filePath);
	    response.setHeader("content-type", "application/octet-stream");
	    response.setContentType("application/octet-stream");
	    response.setHeader("Content-Disposition", "attachment;filename=" + fileName+".mp3");
	    byte[] buff = new byte[1024];
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    try {
	      os = response.getOutputStream();
	      bis = new BufferedInputStream(inputStream);
	      int i = bis.read(buff);
	      while (i != -1) {
	        os.write(buff, 0, buff.length);
	        os.flush();
	        i = bis.read(buff);
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    } finally {
	      if (bis != null) {
	        try {
	          bis.close();
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	      }
	    }
		
		return null;
	};

}
