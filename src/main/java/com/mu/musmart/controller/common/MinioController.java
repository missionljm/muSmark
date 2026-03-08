package com.mu.musmart.controller.common;

import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.service.MinioService;
import com.mu.musmart.domain.vo.ResVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Api(tags = "MinIO文件管理")
@RestController
@RequestMapping("/minio/api")
public class MinioController {

    @Autowired
    private MinioService minioService;

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public ResVo<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = minioService.uploadFile(file);
            return ResVo.ok(fileName);
        } catch (Exception e) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, e.getMessage());
        }
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{fileName}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) {
        try {
            InputStream inputStream = minioService.downloadFile(fileName);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/delete/{fileName}")
    public ResVo<String> deleteFile(@PathVariable String fileName) {
        try {
            minioService.deleteFile(fileName);
            return ResVo.ok("删除成功");
        } catch (Exception e) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR, e.getMessage());
        }
    }

    @ApiOperation("获取文件URL")
    @GetMapping("/url")
    public ResVo<String> getFileUrl(@RequestParam("fileName") String fileName, @RequestParam(defaultValue = "3600") int expires) {
        try {
            String url = minioService.getFileUrl(fileName, expires);
            return ResVo.ok(url);
        } catch (Exception e) {
            return ResVo.fail(StatusEnum.UNEXPECT_ERROR , e.getMessage());
        }
    }
}
