package ru.fomin.service.netty;

import io.netty.channel.ChannelHandlerContext;
import ru.fomin.dto.file_packages.FileChunkPackage;
import ru.fomin.dto.file_packages.FileDataPackage;

import java.io.IOException;

/**
 * Service for process FileDataPackage and FileChunkPackage messages from client.
 */
public interface DownloadService {

    /**
     * Creates new file by one iteration.
     */
    void downloadSmallFile(ChannelHandlerContext ctx, FileDataPackage pack) throws IOException;

    /**
     * Delegates processing chunk of file to FileChunkDownloaderService.
     */
    void downloadBigFile(ChannelHandlerContext ctx, FileChunkPackage pack) throws IOException;

}
