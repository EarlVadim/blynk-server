package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.ByteUtils;
import cc.blynk.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static cc.blynk.server.core.protocol.enums.Command.LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Response.NO_DATA;
import static cc.blynk.utils.ByteBufUtil.makeBinaryMessage;
import static cc.blynk.utils.ByteBufUtil.makeResponse;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class LoadProfileGzippedLogic {

    private static final Logger log = LogManager.getLogger(LoadProfileGzippedLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String body;
        //load all
        if (message.length == 0) {
            body = user.profile.toString();
        } else {
            //load specific by id
            int dashId = ParseUtil.parseInt(message.body);
            body = user.profile.getDashByIdOrThrow(dashId).toString();
        }

        try {
            byte[] data = ByteUtils.compress(body);
            ctx.writeAndFlush(makeBinaryMessage(LOAD_PROFILE_GZIPPED, message.id, data), ctx.voidPromise());
        } catch (IOException e) {
            log.error("Error compressing data.", e);
            ctx.writeAndFlush(makeResponse(message.id, NO_DATA));
        }
    }

}
