package edu.uestc.board;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
// TODO: 选择合适的数据结构（提示：并发场景）
import java.util.concurrent.CopyOnWriteArrayList;
// import java.util.ArrayList;

/**
 * 留言板Servlet类
 * 处理用户留言的展示和提交功能
 * 支持并发访问，确保线程安全
 */
@WebServlet("/board")
public class BoardServlet extends HttpServlet {
    // TODO 1: 用合适的线程安全集合替换下面这一行（当前仅为占位）
    /**
     * 存储留言信息的线程安全集合
     * 使用CopyOnWriteArrayList确保在并发环境下的线程安全
     * 适用于读多写少的场景，如留言板这种读操作远多于写操作的场景
     * 每次写操作都会创建一个新的数组副本，读操作不需要加锁，提高并发性能
     */
    private final List<Message> messages = new java.util.concurrent.CopyOnWriteArrayList<>();

    /**
     * 处理GET请求，展示留言板页面
     * 包含留言表单和已有留言列表
     * 
     * @param req  HttpServletRequest对象，包含客户端请求信息
     * @param resp HttpServletResponse对象，用于向客户端发送响应
     * @throws IOException 当IO操作出现异常时抛出
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置响应内容类型和字符编码，确保正确显示中文
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 输出HTML页面头部和留言表单
        // 使用Java 15的文本块特性简化HTML代码编写
        out.println("""
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"><title>Mini Board</title></head>
            <body>
            <h2>Mini Message Board</h2>
            <form method="post" action="board">
                Nickname: <input name="nickname" maxlength="20" required>
                <br>Message: <input name="content" maxlength="140" required style="width:320px">
                <br><button type="submit">Post</button>
            </form><hr>
            <h3>Messages</h3>
            """);

        // TODO 2: 按"最新在上"的顺序输出 messages
        // 需要对 nickname 与 content 做 HTML 转义（调用你在 TODO 4 实现的方法）
        // 示例占位：
        // 检查是否有留言，如果没有则显示提示信息
        if (messages.isEmpty()) {
            // 当没有留言时显示提示信息
            out.println("<p><i>(No messages yet)</i></p>");
        } else {
            // 倒序遍历列表，以显示最新留言在最上方
            // 从messages.size()-1开始，到0结束，步长为-1
            for (int i = messages.size() - 1; i >= 0; i--) {
                Message msg = messages.get(i);
                // 使用HTML转义方法防止XSS攻击，输出留言信息
                // 格式：昵称: 留言内容
                out.printf("<p><b>%s</b>: %s</p>", esc(msg.getNickname()), esc(msg.getContent()));
            }
        }
        // 结束HTML页面
        out.println("</body></html>");
    }

    /**
     * 处理POST请求，接收并保存用户提交的留言
     * 
     * @param req  HttpServletRequest对象，包含用户提交的表单数据
     * @param resp HttpServletResponse对象，用于向客户端发送响应
     * @throws IOException 当IO操作出现异常时抛出
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO 3: 从请求中取 nickname 与 content，做基本校验（非空、长度限制）
        // 从请求参数中获取昵称和留言内容
        String nickname = req.getParameter("nickname");
        String content = req.getParameter("content");

        // 简单校验：确保昵称和内容都不为空且不只包含空白字符
        // 使用trim()方法去除首尾空格后再判断是否为空
        if (nickname!= null &&!nickname.trim().isEmpty() && content!= null &&!content.trim().isEmpty()) {
            // 创建新的留言对象并添加到留言列表中
            // 由于使用CopyOnWriteArrayList，add操作是线程安全的
            Message newMessage = new Message(nickname, content);
            messages.add(newMessage);
        }

        // 完成后重定向回 GET /board，刷新页面显示最新留言
        // 使用重定向而非直接转发，避免用户刷新页面时重复提交表单
        resp.sendRedirect("board");
    }

    // TODO 4: 实现一个最小的 HTML 转义方法，防止 XSS
    // 要至少处理 &, <, >
    /**
     * 对字符串进行HTML转义，防止XSS攻击
     * 将特殊HTML字符转换为对应的HTML实体
     * 
     * @param s 需要转义的原始字符串
     * @return 转义后的字符串，如果输入为null则返回空字符串
     */
    private String esc(String s) {
        // 防止空指针异常，如果输入为null则返回空字符串
        if (s == null) {
            return "";
        }
        // 依次替换特殊字符为HTML实体，顺序很重要，必须先替换&符号
        return s.replace("&", "&amp;")     // & 转换为 &amp;，必须放在第一位
                .replace("<", "&lt;")      // < 转换为 &lt;
                .replace(">", "&gt;")      // > 转换为 &gt;
                .replace("\"", "&quot;")   // " 转换为 &quot;
                .replace("'", "&#39;");    // ' 转换为 &#39;
    }
}