package aopTest.test;

import java.security.GeneralSecurityException;
import java.util.Properties;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailTest {

    public EmailTest(){
    }
    /**
     * 设置邮件参数
     * @param serverHost 邮件服务器主机
     */
    public Properties setProperties(String serverHost){
        Properties properties = System.getProperties();
        properties.setProperty("mail.stmp.host",serverHost);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return properties;
    }

    public static void main(String[] args){
        EmailTest test = new EmailTest();
        String to= "669545057@qq.com";
//        String from = "1312750582@qq.com";
        String from = "longge95@2980.com";
        String password = "long95";
        String host = "smtp.2980.com";
        Properties properties = test.setProperties(host);
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
//                return new PasswordAuthentication("1312750582@qq.com", "HXLhxl131275"); //发件人邮件用户名、密码
                return new PasswordAuthentication(from, password); //发件人邮件用户名、密码
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject("This is the Subject Line!");

            // 设置消息体
            message.setText("This is actual message");

            // 通过 session 得到 transport 对象
            Transport ts = session.getTransport();

            // 使用邮箱的用户名和授权码连上邮箱服务器
            ts.connect(host, from, password);
            // 发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            System.out.println("验证码发送成功");
            // 释放资源
            ts.close();
            // 发送消息
//            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");


        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}


//public class EmailTest {
//    public static void main(String[] args) throws Exception {
//        testMail();
//    }
//
//    private static void testMail () throws Exception {
//        // 给用户发送邮件的邮箱
//        final String from = "1312750582@qq.com";
//        // 邮箱的用户名
//        final String username = "1312750582@qq.com";
//        // 邮箱授权码，刚刚保存的授权码，不是qq密码
////        final String password = "oiknpwwrimuahghf";
//        //QQ邮箱授权码有效期只有15分钟，过期失效
//        final String password = "zumqjcpwxtwyichg";
//
//        // 发送邮件的服务器地址，QQ服务器
//        final String host = "smtp.qq.com";
//        // 接收人邮箱
//        final String to = "669545057@qq.com";
//        // 邮件主题
//        final String title = "验证码发送";
//
//        // 使用QQ邮箱时配置
//        Properties prop = new Properties();
//        prop.setProperty("mail.host", "smtp.qq.com");    // 设置QQ邮件服务器
//        prop.setProperty("mail.transport.protocol", "smtp");      // 邮件发送协议
//        prop.setProperty("mail.smtp.auth", "true");      // 需要验证用户名和密码
//        // 关于QQ邮箱，还要设置SSL加密，其他邮箱不需要
//        MailSSLSocketFactory sf = new MailSSLSocketFactory();
//        sf.setTrustAllHosts(true);
//        prop.put("mail.smtp.ssl.enable", "true");
//        prop.put("mail.smtp.ssl.socketFactory", sf);
//
//        // 创建定义整个邮件程序所需的环境信息的 Session 对象，QQ才有，其他邮箱就不用了
//        Session session = Session.getDefaultInstance(prop, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                // 发件人邮箱用户名，授权码
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        // 开启 Session 的 debug 模式，这样就可以查看程序发送 Email 的运行状态
//        session.setDebug(true);
//
//        // 通过 session 得到 transport 对象
//        Transport ts = session.getTransport();
//
//        // 使用邮箱的用户名和授权码连上邮箱服务器
//        ts.connect(host, username, password);
//
//        // 创建邮件，写邮件
//        MimeMessage message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(from)); // 指明邮件的发件人
//        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));   // 指明邮件的收件人
//        message.setSubject(title);     // 邮件主题
//        message.setContent("验证码为:8824", "text/html;charset=utf-8");    // 邮件内容
//
//        // 发送邮件
//        ts.sendMessage(message, message.getAllRecipients());
//        System.out.println("验证码发送成功");
//        // 释放资源
//        ts.close();
//    }
//}
