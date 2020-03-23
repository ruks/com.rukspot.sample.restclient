/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package com.rukspot.sample.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Simple WebSocket server for Test cases.
 */
public final class WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private final int port;
    private String subProtocols = null;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public WebSocketServer(int port) {
        this.port = port;
    }

    public WebSocketServer(int port, String subProtocols) {
        this.port = port;
        this.subProtocols = subProtocols;
    }

    public void run() throws InterruptedException {
        final SslContext sslCtx = null;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(2);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .childHandler(new WebSocketRemoteServerInitializer(sslCtx, subProtocols));

        serverBootstrap.bind(port).sync();
        logger.info("WebSocket remote server started listening on port " + port);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("WebSocket remote server stopped listening  on port " + port);
    }

    public static void main(String[] args) throws Exception {
        WebSocketServer webSocketServer = new WebSocketServer(7474);
        webSocketServer.run();

//        if(true) return;
        int latchCountDownInSecs = 30;
        CountDownLatch latch = new CountDownLatch(1);
//        WebSocketTestClient webSocketTestClient = new WebSocketTestClient("ws://localhost:7474/", latch);
        WebSocketTestClient webSocketTestClient = new WebSocketTestClient("ws://localhost:9099/websocket/1.0.0", latch);
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("Authorization","Bearer db877116-8f07-3a6b-92fb-44d212eb1e8e");
        customHeaders.put("websocket-ruks-header.rukshan","Bearer ae39b3bd-ca6c-3592-8ac0-facfabee7042");
        webSocketTestClient.setCustomHeaders(customHeaders);
        if(webSocketTestClient.handhshake()) {

        } else {
            System.out.println("Web Socket Handshake failed");
        }
        String text = "{message:\"hello web socket test\"}";
        webSocketTestClient.sendText(text);
        latch.await(latchCountDownInSecs, TimeUnit.SECONDS);
        System.out.println(webSocketTestClient.getTextReceived());

//        System.exit(0);
    }
}
