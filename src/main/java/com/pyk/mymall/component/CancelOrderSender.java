package com.pyk.mymall.component;

import com.pyk.mymall.dto.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 取消订单消息的发出者.
 * 用于向订单延迟消息队列（mall.order.cancel.ttl）里发送消息。
 * Created by pangyankun on 2019/12/30.
 */
@Component
public class CancelOrderSender {

    private static Logger LOGGER = LoggerFactory.getLogger(CancelOrderSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void  sendMessage(Long orderId,final long delayTime){
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange(), QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey(), orderId, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(String.valueOf(delayTime));
                return message;
            }
        });
        LOGGER.info("send delay message orderId:{}",orderId);
    }
}
