package controllers;

import informWindows.InformWindow;
import javafx.scene.control.Label;
import javafx.stage.Window;
import obgect.Reservation;
import obgect.User;
import obgektDB.ReservationDb;
import obgektDB.SitesDB;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import obgect.Sites;
import obgektDB.UserDB;
import utilits.Util_Sites;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddSites_Controller {
    @FXML
    TextField nomer_Sites;
    @FXML
    TextField adress_Sites;

    private User user;
    private  ObservableList<Sites> listSites;
    private long USER_ADMIN = 3;
    private int TIME_MICRO = 1000;

    public void setParentWindow(Window parentWindow) {
        this.parentWindow = parentWindow;
    }

    private Window parentWindow;

    @FXML
    private void initialize(){

    }

    public void addAction(ActionEvent actionEvent) {
        addSites();

    }

    private void addListSites(Sites sites){
        listSites.add(sites);
    }
    private void windowClose(ActionEvent actionEvent){
        Node sourse = (Node) actionEvent.getSource();
        Stage stage = (Stage)sourse.getScene().getWindow();
        Window parentWindow = stage.getOwner();


    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void addSites(){

        if(Util_Sites.getInstance().chekTextFaild(adress_Sites)&&Util_Sites.getInstance().chekTextFaild(nomer_Sites)) {//проверка на пустые строки
            Sites sites = new Sites();
            try {
                int nomer = Integer.parseInt(nomer_Sites.getText());
                if(!SitesDB.getInstance().chekSitesByNumber(nomer,user.getCollection_id())) {
                    sites.setNumber(nomer);
                    sites.setAdress(adress_Sites.getText());
                    sites.setCollection_id(user.getCollection_id());
                    if(Util_Sites.getInstance().writeSitesFromBD(sites)) {
                        createReservation(sites.getId());
                        addListSites(sites);
                        adress_Sites.clear();
                        nomer_Sites.clear();
                    }
                    else {
                        InformWindow informWindow = new InformWindow();
                        informWindow.errorWindow("Ошибка соединения с Базой данных","Не удалось создать '\n'Проверте соединение");
                    }
                }
                else {
                    InformWindow informWindow = new InformWindow();
                    informWindow.informWindow("Ошибка данных","Такой номер уже существует");
                }
            }
            catch (NumberFormatException ex){
                InformWindow informWindow = new InformWindow();
                informWindow.informWindow("Ошибка данных","Номер участка должен состоять только из цифр");
            }
            catch (SQLException ex){
                InformWindow informWindow = new InformWindow();
                informWindow.errorWindow("Ошибка соединения с Базой данных","Не удалось создать '\n'Проверте соединение");
            }
            catch (NullPointerException ex){

            }

        }
        else {
            InformWindow informWindow = new InformWindow();
            informWindow.informWindow("Ошибка данных","Все поля должны быть заполнены");
            adress_Sites.setText("Введите данные");
            nomer_Sites.setText("Введите данные");
        }
    }




  private void createReservation(long sites_id ){
      Reservation reservation = new Reservation();
      reservation.setUser(UserDB.getInstance().createObgectbyId(USER_ADMIN));//что будет если этого юзера удалят?
      reservation.setSites_id(sites_id);
      reservation.setDate_issue(0);
      Calendar calendar = new GregorianCalendar();
      long time = calendar.getTimeInMillis()/TIME_MICRO;
      reservation.setDate_delivery(time);
      ReservationDb.getInstance().insertObgect(reservation);


  }

    public void setNomer_Sites(TextField nomer_Sites) {
        this.nomer_Sites = nomer_Sites;
    }

    public void setList(ObservableList<Sites> list) {
        this.listSites = list;
    }

}