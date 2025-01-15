package com.example.demo1.controller;

import com.example.demo1.entities.ClassRoom;
import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/classroom")

public class ClassRoomController extends HttpServlet {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @Override
    public void init() throws ServletException {
        super.init();
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        System.out.println("ClassRoom doGet");
        var classRooms = entityManager.createStoredProcedureQuery("GetAllClassRooms", ClassRoom.class).getResultList();
        req.setAttribute("classRoomList", classRooms);
        req.getRequestDispatcher("/views/classroom.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("ClassRoom doPost");

             var class_name = req.getParameter("class_name");
            var number = Integer.valueOf(req.getParameter("number_member"));

            entityManager.getTransaction().begin();

            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("InsertClassRoom");
            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);  // class_name
            query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN); // number_member

            query.setParameter(1, class_name);
            query.setParameter(2, number);

            query.execute();

            entityManager.getTransaction().commit();

            resp.sendRedirect("classroom");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi xử lý yêu cầu.");
        }
    }
}
