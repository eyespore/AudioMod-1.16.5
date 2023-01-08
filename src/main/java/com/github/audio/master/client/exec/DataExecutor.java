package com.github.audio.master.client.exec;

import com.github.audio.master.client.ClientExecutor;
import com.github.audio.master.net.DataPacket;
import com.github.audio.registryHandler.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.*;

@OnlyIn(Dist.CLIENT)
public class DataExecutor extends ClientExecutor {
    private static final DataExecutor DATA_EXECUTOR = new DataExecutor();
    private DataExecutor() {}
    public static DataExecutor getDataExecutor() {
        return DATA_EXECUTOR;
    }

    public byte[] splitMusicFile(RandomAccessFile raf,long pos, int len) throws IOException {
        byte[] ret = new byte[len];
        raf.seek(pos);
        raf.read(ret, 0, len);
        return ret;
    }

    public void transData(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        for (long i = 0; file.length() - i * 1024 * 31 > 0; i++) {
            if (file.length() - i * 1024 * 31 < 1024 * 31) {
                NetworkHandler.DATA_CHANNEL.sendToServer(new DataPacket(file.getName() + ":" + i, splitMusicFile(raf, i * 1024 * 31, (int) (file.length() - i * 1024 * 31))));
                break;
            }
            NetworkHandler.DATA_CHANNEL.sendToServer(new DataPacket(file.getName() + ":" + i, splitMusicFile(raf, i * 1024 * 31, 1024 * 31)));
        }
        raf.close();
    }
}
