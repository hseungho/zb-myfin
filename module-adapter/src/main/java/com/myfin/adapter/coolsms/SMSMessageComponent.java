package com.myfin.adapter.coolsms;

import com.myfin.core.exception.impl.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SMSMessageComponent {

    private final DefaultMessageService messageService;
    private final String fromNumber;

    public SMSMessageComponent(@Value("${cool-sms.api-key}") final String apiKey,
                               @Value("${cool-sms.api-secret-key}") final String apiSecretKey,
                               @Value("${cool-sms.domain}") final String domain,
                               @Value("${cool-sms.from-number}") final String fromNumber) {

        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, domain);
        this.fromNumber = fromNumber;

    }

    public void sendMessage(final String toNumber, final String verifyCode) {
        Message message = new Message();
        message.setFrom(this.fromNumber);
        message.setTo(toNumber.replace("-", ""));
        message.setText("[마이핀페이] 본인확인 인증번호["+verifyCode+"]를 입력해주세요.");

        try {
            this.messageService.send(message);

        } catch (Exception e) {
            final String toNum = toNumber.replace("-", "")
                            .replace(toNumber.substring(3, 7), "****");

            log.error("Occurred Exception during send SMS to "+toNum+".", e);

            throw new InternalServerException("본인확인 인증 메시지 발송 중에 오류가 발생하였습니다. 관리자에게 문의해주세요.");
        }
    }

}
