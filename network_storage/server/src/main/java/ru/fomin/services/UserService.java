package ru.fomin.services;

import ru.fomin.dao.UserDao;

public class UserService {

   private final static UserDao USER_DAO =new UserDao();

   public static boolean isUserExist(String login){
      if(USER_DAO.getUsersByLogin(login)==null){
         return false;
      }
      return true;
   }
}
