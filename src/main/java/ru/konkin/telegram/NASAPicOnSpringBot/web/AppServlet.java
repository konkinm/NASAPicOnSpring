package ru.konkin.telegram.NASAPicOnSpringBot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "MainServlet", urlPatterns = "/")
public class AppServlet extends HttpServlet {
    private ObjectMapper mapper;
    private Handler handler;
    @Override
    public void init() throws ServletException {
        super.init();
        mapper = new ObjectMapper();
        handler = new Handler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.getOutputStream().println("OK!");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        Update update = mapper.readValue(reader.lines()
                .reduce(String::concat).orElseThrow(IOException::new), Update.class);
        SendMessage message = handler.apply(update);
        String messageJson = mapper.writeValueAsString(message);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        final PrintWriter out = response.getWriter();
        out.print(messageJson);
        out.flush();
        System.out.println("Message sent to chat with id=" + message.getChatId());
    }
}