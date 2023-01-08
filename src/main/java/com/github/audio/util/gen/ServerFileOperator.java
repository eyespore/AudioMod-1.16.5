package com.github.audio.util.gen;

import com.github.audio.api.ByteTransformer;

/**
 * @author clclFL
 * @Description: The operator for operating the file in the server side, which is mainly for operate the file that all the
 * players use together.
 */
public class ServerFileOperator {

    private static final ByteTransformer FILE_TRANSFORMER = ByteTransformer.getByteTransformer();

    private ServerFileOperator() {}

    private static final class ServerFileOperatorHolder {
        private static final ServerFileOperator SERVER_FILE_OPERATOR = new ServerFileOperator();
    }

    public static ServerFileOperator getServerFileOperator() {
        return ServerFileOperatorHolder.SERVER_FILE_OPERATOR;
    }

}
