# JrmfNetEaseDemo-Android
实现在云信IM场景中的红包收发功能

## 1 产品说明
利用金融魔方的红包和钱包功能整合云信即时通讯的功能，实现在聊天场景中收发红包的功能；同时集成金融魔方的钱包产品，显示余额变动，提供充值提现等功能

## 2 集成步骤
1. 利用云信的消息机制自定义红包消息附件RedPacketAttachment和拆红包消息附件RedPacketOpenedAttachment
2. 定义展示红包消息和拆红包消息的ViewHolder,分别为：MsgViewHolderRedPacket和MsgViewHolderOpenRedPacket
3. 定义消息的入口RedPacketAction，在会话页面中点击“+”号，展示消息类型
4. 定义消息解析器用来解析红包消息和拆红包消息
5. 在您的Application中调用方法NIMRedPacketClient.init(this)初始化红包 
6. 在您调用金融魔方的各个接口之前请先获得token，这是您使用红包和钱包功能的身份证明

