import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

// ==========================================
// CORE COMPONENT: ABSTRACT MESSAGE
// ==========================================

abstract class Message {
    protected String sender;
    protected String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() { return sender; }
    public String getContent() { return content; }
    public abstract String getDisplayText();
}

class TextMessage extends Message {
    public TextMessage(String sender, String content) {
        super(sender, content);
    }
    @Override
    public String getDisplayText() { return sender + ": " + content; }
}

class SystemMessage extends Message {
    public SystemMessage(String sender, String content) {
        super(sender, content);
    }
    @Override
    public String getDisplayText() { return "[SYSTEM] " + content; }
}


// ==========================================
// FACTORY PATTERN
// ==========================================

class MessageFactory {
    public static Message createMessage(String type, String sender, String content) {
        if (type.equalsIgnoreCase("text")) {
            return new TextMessage(sender, content);
        } else if (type.equalsIgnoreCase("system")) {
            return new SystemMessage("SYSTEM", content);
        } else {
            return new TextMessage(sender, "Error: Unknown message type.");
        }
    }
}


// ==========================================
// DECORATOR PATTERN
// ==========================================

abstract class MessageDecorator extends Message {
    protected Message wrappedMessage;

    public MessageDecorator(Message message) {
        super(message.getSender(), message.getContent());
        this.wrappedMessage = message;
    }

    @Override
    public String getDisplayText() {
        return wrappedMessage.getDisplayText();
    }

    public Message getWrappedMessage() {
        return wrappedMessage;
    }
}

class TimestampDecorator extends MessageDecorator {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TimestampDecorator(Message message) {
        super(message);
    }

    @Override
    public String getDisplayText() {
        String time = LocalTime.now().format(TIME_FORMAT);
        return "[" + time + "] " + wrappedMessage.getDisplayText();
    }
}


// ==========================================
// OBSERVER PATTERN
// ==========================================

interface ChatObserver {
    void update(Message message);
}

class Subject {
    private List<ChatObserver> observers = new ArrayList<>();

    public void attach(ChatObserver observer) { observers.add(observer); }
    public void notifyObservers(Message message) {
        for (ChatObserver observer : observers) {
            observer.update(message);
        }
    }
}


// ==========================================
// SINGLETON PATTERN: ChatEngine
// ==========================================

class ChatEngine extends Subject {
    private static ChatEngine instance;

    private ChatEngine() {
        System.out.println("ChatEngine initialized (Singleton).");
    }

    public static synchronized ChatEngine getInstance() {
        if (instance == null) instance = new ChatEngine();
        return instance;
    }

    public void sendMessage(Message message) {
        notifyObservers(message);
    }
}


// ==========================================
// BUILDER PATTERN: ChatSession
// ==========================================

class ChatSession {
    private String username;
    private String theme;

    public void setUsername(String username) { this.username = username; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getUsername() { return username; }
    public String getTheme() { return theme; }
}

class ChatSessionBuilder {
    private ChatSession session = new ChatSession();

    public ChatSessionBuilder setUsername(String username) {
        session.setUsername(username);
        return this;
    }

    public ChatSessionBuilder setTheme(String theme) {
        session.setTheme(theme);
        return this;
    }

    public ChatSession build() {
        if (session.getUsername() == null || session.getUsername().isEmpty()) {
            session.setUsername("Guest-" + new Random().nextInt(1000));
        }
        if (session.getTheme() == null) session.setTheme("Light Mode");
        return session;
    }
}


// ==========================================
// GUI + OBSERVER (MAIN CLASS)
// ==========================================

public class Ahmed extends JFrame implements ChatObserver {

    private final ChatEngine engine;
    private final ChatSession session;
    private JTextPane chatArea;
    private JTextField inputField;
    private JCheckBox timestampCheck;
    private StyledDocument doc;

    public Ahmed() {
        engine = ChatEngine.getInstance();
        engine.attach(this);

        session = new ChatSessionBuilder()
                .setUsername("Student_User")
                .setTheme("Dark Mode")
                .build();

        setupUI();
        startSimulation();
    }

    private void setupUI() {
        setTitle("Design Pattern Chat Simulator");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(200, 220, 255));
        JLabel statusLabel = new JLabel("Welcome, " + session.getUsername() + " | Theme: " + session.getTheme());
        statusLabel.setForeground(new Color(0, 51, 153));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(statusLabel);
        add(headerPanel, BorderLayout.NORTH);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setText("--- Chat Session Started ---\n\n");
        doc = chatArea.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(500, 30));

        JButton sendTextBtn = new JButton("Send Message");
        sendTextBtn.addActionListener(e -> sendMessage("text"));

        JButton sendSystemBtn = new JButton("Send System Message");
        sendSystemBtn.addActionListener(e -> sendMessage("system"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendSystemBtn);
        buttonPanel.add(sendTextBtn);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        timestampCheck = new JCheckBox("Include Timestamp", true);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(timestampCheck, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private Message unwrapMessage(Message msg) {
        if (msg instanceof MessageDecorator) {
            return unwrapMessage(((MessageDecorator) msg).getWrappedMessage());
        }
        return msg;
    }

    private void sendMessage(String type) {
        String content = inputField.getText().trim();
        if (content.isEmpty() && type.equals("text")) return;

        if (content.isEmpty() && type.equals("system")) {
            content = "Default system alert sent by user.";
        }

        Message msg = MessageFactory.createMessage(type, session.getUsername(), content);

        if (timestampCheck.isSelected()) {
            msg = new TimestampDecorator(msg);
        }

        engine.sendMessage(msg);
        inputField.setText("");
    }

    @Override
    public void update(Message message) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleAttributeSet style = new SimpleAttributeSet();
                Message base = unwrapMessage(message);

                if (base instanceof SystemMessage) {
                    StyleConstants.setForeground(style, Color.RED);
                    StyleConstants.setBold(style, true);
                } else {
                    StyleConstants.setForeground(style, Color.BLACK);
                }

                doc.insertString(doc.getLength(), message.getDisplayText() + "\n", style);
                chatArea.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    private void startSimulation() {
        new Thread(() -> {
            String[] userNames = {"Ahmed", "Bot_Support", "Ahmed"};
            String[] commonMessages = {"Hello!", "Testing...", "Good work!", "Bug spotted.", "Any update?"};
            String[] systemMessages = {"Server restarting...", "Maintenance soon.", "Patch applied."};

            Random r = new Random();

            try {
                Thread.sleep(2000);
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(2000 + r.nextInt(3000));

                    Message msg;
                    if (r.nextInt(10) < 2) {
                        msg = MessageFactory.createMessage("system", "SYSTEM", systemMessages[r.nextInt(systemMessages.length)]);
                    } else {
                        msg = MessageFactory.createMessage("text", userNames[r.nextInt(userNames.length)],
                                commonMessages[r.nextInt(commonMessages.length)]);
                    }

                    msg = new TimestampDecorator(msg);
                    engine.sendMessage(msg);
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ahmed app = new Ahmed();
            app.setVisible(true);
        });
    }
}
