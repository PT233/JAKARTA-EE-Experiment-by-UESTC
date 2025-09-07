package edu.uestc.board;

/**
 * 留言板消息实体类
 * 用于封装用户留言的基本信息，包括昵称和留言内容
 * 设计为不可变类，确保线程安全和数据一致性
 */
public class Message {
    // TODO 1: 定义字段（建议使用不可变：private final String nickname/content）
    /**
     * 用户昵称，使用final修饰确保不可变性
     */
    private final String nickname;
    /**
     * 留言内容，使用final修饰确保不可变性
     */
    private final String content;

    // TODO 2: 构造方法，接收 nickname 与 content 并赋值
    /**
     * Message类的构造方法
     * @param nickname 用户昵称，不能为空
     * @param content 留言内容，不能为空
     */
    public Message(String nickname, String content) {
        this.nickname = nickname;
        this.content = content;
    }

    // TODO 3: 提供 getter（无需 setter）
    /**
     * 获取用户昵称
     * @return 返回用户昵称字符串
     */
    public String getNickname() {
        return nickname; // TODO
    }

    /**
     * 获取留言内容
     * @return 返回留言内容字符串
     */
    public String getContent() {
        return content; // TODO
    }
}