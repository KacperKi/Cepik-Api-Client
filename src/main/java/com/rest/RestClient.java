package com.rest;

import com.google.gson.JsonObject;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class RestClient {

    ArrayList<String> nameOfColumnsFromFile = new ArrayList<String>() {
        {
            add("marka");add("kategoria");add("typ");add("model");add("wariant");add("rodzaj");add("pochodzenie");add("rok");add("dataResjestracji");add("pojemnosc");add("masa");add("paliwo");
        }
    };
    ArrayList<ArrayList<String>> daneDoTabeli = new ArrayList<>();
    ArrayList<ArrayList<String>> dataFromAPI = new ArrayList<>();

    JFrame userFrame;
    JTable tableWithData;
    JScrollPane scrollPane;
    DefaultTableModel model;
    JLabel startDateInfo, endDateInfo, numberOfRows, regionInfo;
    JComboBox regionNumber;
    JButton getDateFromAPI, filterByProducer;
    JDatePanelImpl startDatePanel, endDatePanel;
    JDatePickerImpl startDate, endDate;
    UtilDateModel modelStartDate, modelEndDate;
    JTextField producerName;
    CepikRepository cepikRepository;

    private String datePattern = "yyyy-MM-dd";

    ArrayList<Pojazd> pojazdy;
    public static void main(String[] args) {
        RestClient restClient = new RestClient();
    }
    public RestClient() {
        CreateClientFrame();
        cepikRepository = new CepikRepository();

    }
    void CreateClientFrame(){
        userFrame = new JFrame("Integracja Systemów - Aplikacja Klienta - Kacper Kisielewski");
        userFrame.setSize(1280, 130);

        CreateDatePickers();
        CreateFilterField();

        getDateFromAPI = new JButton("Get Data From API");
        getDateFromAPI.setBounds(900,10,150,28);

        numberOfRows = new JLabel();
        numberOfRows.setBounds(820,10,80,28);
        numberOfRows.setText("Rows:  0");
        userFrame.add(numberOfRows);

        userFrame.add(getDateFromAPI);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        userFrame.setLocation(dim.width/2 - userFrame.getWidth()/2, dim.height/2 + userFrame.getHeight());
        userFrame.setLayout(null);
        userFrame.setVisible(true);

        CreateListener();
    }
    void CreateListener(){
        getDateFromAPI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GetDataFromAPIClickedButton();
            }
        });
    }
    void CreateFilterField(){
        filterByProducer = new JButton("Search");
        producerName = new JTextField("Type producer name");

        producerName.setBounds(310,50,150, 30);
        filterByProducer.setBounds(460,50,150, 30);

        filterByProducer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterDataInTable();
            }
        });

        userFrame.add(producerName); userFrame.add(filterByProducer);
    }
    void FilterDataInTable(){
        String producerNameFromFiled = producerName.getText();
        if(!producerNameFromFiled.isEmpty() && !producerNameFromFiled.equals("Type producer name")){
            for(ArrayList<String> row : dataFromAPI){
                if(!row.contains(producerNameFromFiled)) this.daneDoTabeli.remove(row);
            }
        }else{
            if(producerNameFromFiled.isEmpty()){
                this.daneDoTabeli = new ArrayList<>();
                this.daneDoTabeli.addAll(dataFromAPI);
            }
        }
        ShowTable(this.daneDoTabeli);
    }
    void GetDataFromAPIClickedButton(){
        String startDateValue = startDate.getJFormattedTextField().getText().replace("-", "");
        String endDateValue = endDate.getJFormattedTextField().getText().replace("-", "");
        String regionNumberNumber = "";

        if(!startDateValue.isEmpty() && !endDateValue.isEmpty()) {

            Date startD = (Date) startDate.getModel().getValue();
            Date endD = (Date) endDate.getModel().getValue();

            if(!startD.after(endD) || !endD.before(startD)) {

                pojazdy = new ArrayList<>();
                switch (String.valueOf(regionNumber.getSelectedItem())) {
                    case "dolnośląskie":
                        regionNumberNumber = "02";
                        break;
                    case "kujawsko-pomorskie":
                        regionNumberNumber = "04";
                        break;
                    case "lubelskie":
                        regionNumberNumber = "08";
                        break;
                    case "lubuskie":
                        regionNumberNumber = "10";
                        break;
                    case "łódzkie":
                        regionNumberNumber = "12";
                        break;
                    case "małopolskie":
                        regionNumberNumber = "14";
                        break;
                    case "mazowieckie":
                        regionNumberNumber = "16";
                        break;
                    case "opolskie":
                        regionNumberNumber = "16";
                        break;
                    case "podkarpackie":
                        regionNumberNumber = "18";
                        break;
                    case "podlaskie":
                        regionNumberNumber = "20";
                        break;
                    case "pomorskie":
                        regionNumberNumber = "22";
                        break;
                    case "śląskie":
                        regionNumberNumber = "24";
                        break;
                    case "świętokrzyskie":
                        regionNumberNumber = "26";
                        break;
                    case "warmińsko-mazurskie":
                        regionNumberNumber = "28";
                        break;
                    case "wielkopolskie":
                        regionNumberNumber = "30";
                        break;
                    case "zachodniopomorskie":
                        regionNumberNumber = "32";
                        break;
                }

                try {
                    JsonObject o = cepikRepository.getPojazdy(regionNumberNumber, startDateValue, endDateValue);
                    JSONObject o1 = new JSONObject(o.toString());
                    JSONArray jsonArray = o1.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject ob = jsonObject.getJSONObject("attributes");

                        pojazdy.add(new Pojazd(
                                ob.getString("marka"),
                                ob.getString("kategoria-pojazdu"),
                                ob.getString("typ"),
                                ob.getString("model"),
                                ob.getString("wariant"),
                                ob.getString("rodzaj-pojazdu"),
                                ob.getString("pochodzenie-pojazdu"),
                                ob.getString("rok-produkcji"),
                                ob.getString("data-pierwszej-rejestracji-w-kraju"),
                                ob.getString("pojemnosc-skokowa-silnika"),
                                ob.getString("masa-wlasna"),
                                ob.getString("rodzaj-paliwa")
                        ));
                    }
                    this.dataFromAPI = ConvertToDoubleArrayList(pojazdy);
                    this.daneDoTabeli = ConvertToDoubleArrayList(pojazdy);
                    ShowTable(daneDoTabeli);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }else{
                JOptionPane.showMessageDialog(userFrame,
                        "Value of start/end date isn't correct!",
                        "Warning!", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(userFrame,
                    "Problem with null value\nin args fields start date or end date!",
                    "Warning! Null value detected!", JOptionPane.WARNING_MESSAGE);
        }
    }
    void CreateDatePickers(){
        modelStartDate = new UtilDateModel();
        modelEndDate = new UtilDateModel();

        startDateInfo = new JLabel("Start DATE: ");
        endDateInfo = new JLabel("End DATE: ");
        regionInfo = new JLabel("Region: ");

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        startDatePanel = new JDatePanelImpl(modelStartDate, p);
        endDatePanel = new JDatePanelImpl(modelEndDate, p);

        startDate = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        endDate = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());

        startDateInfo.setBounds(30,10,150, 30);
        endDateInfo.setBounds(300,10,100, 30);

        startDate.setBounds(110,10,150,50);
        endDate.setBounds(380,10,150,50);

        userFrame.add(startDateInfo); userFrame.add(endDateInfo);
        userFrame.add(startDate); userFrame.add(endDate);

        String[] wojewodztwaList = {
                "dolnośląskie",
                "kujawsko-pomorskie",
                "lubelskie",
                "lubuskie",
                "łódzkie",
                "małopolskie",
                "mazowieckie",
                "opolskie",
                "podkarpackie",
                "podlaskie",
                "pomorskie",
                "śląskie",
                "świętokrzyskie",
                "warmińsko-mazurskie",
                "wielkopolskie",
                "zachodniopomorskie"};

        regionNumber = new JComboBox(wojewodztwaList);
        regionInfo.setBounds(560,10,60,30);
        regionNumber.setBounds(620, 10,150,30);
        userFrame.add(regionNumber); userFrame.add(regionInfo);
    }
    void UpdateNumberInfoOfRows(){
        numberOfRows.setText("Rows: " + String.valueOf(this.tableWithData.getRowCount()));
    }
    void ShowTable(ArrayList<ArrayList<String>> dataToTable){
        if(dataToTable.size()==0) {
            JOptionPane.showMessageDialog(
                    userFrame,
                    "0 rows founded for this options!",
                    "Imported data information!",
                    JOptionPane.INFORMATION_MESSAGE);
        }else {
            if (scrollPane == null) {
                this.model = new DefaultTableModel(ConvertDataToObject(dataToTable), nameOfColumnsFromFile.toArray());
                this.tableWithData = new JTable(model);
                this.scrollPane = new JScrollPane(tableWithData, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

                tableWithData.getTableHeader().setReorderingAllowed(false);
                tableWithData.getTableHeader().setResizingAllowed(false);

                scrollPane.setEnabled(false);
                scrollPane.setBounds(10, 85, 1700, 300);
                scrollPane.setVisible(true);
                scrollPane.revalidate();
                scrollPane.repaint();

                this.userFrame.add(scrollPane);

                this.tableWithData.setAutoCreateRowSorter(true);

                userFrame.setSize(1720, 440);
                userFrame.invalidate();
                userFrame.validate();
                userFrame.repaint();
            } else {
                this.model = new DefaultTableModel(ConvertDataToObject(dataToTable), nameOfColumnsFromFile.toArray());
                this.tableWithData.setModel(model);
            }
        }
        UpdateNumberInfoOfRows();
    }

    Object[][] ConvertDataToObject(ArrayList<ArrayList<String>> data){
        int row = data.size();
        int column = data.get(0).size();
        Object[][] result = new Object[row][column];

        int i = 0, j = 0;
        for(ArrayList<String> t: data){
            j = 0;
            for(String el: t){
                if(el == null || el.equals("")) result[i][j] = "brak";
                else result[i][j] = el;
                j++;
            }
            i++;
        }
        return result;
    }
    ArrayList<ArrayList<String>> ConvertToDoubleArrayList(ArrayList<Pojazd> data){
        ArrayList<ArrayList<String>> dataToReturn = new ArrayList<>();
        ArrayList<String> row;
        for(Pojazd pojazd : data){
            row = new ArrayList<>();
            row.add(pojazd.getMarka()); row.add(pojazd.getKategoria_pojazdu());
            row.add(pojazd.getTyp()); row.add(pojazd.getModel());
            row.add(pojazd.getWariant()); row.add(pojazd.getRodzajPojazdu());
            row.add(pojazd.getPochodzenie()); row.add(pojazd.getRokProdukcji());
            row.add(pojazd.getDataRejestracji()); row.add(pojazd.getPojemnosc());
            row.add(pojazd.getMasa()); row.add(pojazd.getRodzajPaliwa());
            dataToReturn.add(row);
        }
        return dataToReturn;
    }
    class Pojazd{
        String marka;
        String kategoria_pojazdu;
        String typ;
        String model;
        String wariant;
        String rodzajPojazdu;
        String pochodzenie;
        String rokProdukcji;
        String dataRejestracji;
        String pojemnosc;
        String masa;
        String rodzajPaliwa;

        public Pojazd(String marka, String kategoria_pojazdu, String typ, String model, String wariant, String rodzajPojazdu, String pochodzenie, String rokProdukcji, String dataRejestracji, String pojemnosc, String masa, String rodzajPaliwa) {
            this.marka = marka;
            this.kategoria_pojazdu = kategoria_pojazdu;
            this.typ = typ;
            this.model = model;
            this.wariant = wariant;
            this.rodzajPojazdu = rodzajPojazdu;
            this.pochodzenie = pochodzenie;
            this.rokProdukcji = rokProdukcji;
            this.dataRejestracji = dataRejestracji;
            this.pojemnosc = pojemnosc;
            this.masa = masa;
            this.rodzajPaliwa = rodzajPaliwa;
        }

        public String getMarka() {
            return marka;
        }

        public void setMarka(String marka) {
            this.marka = marka;
        }

        public String getKategoria_pojazdu() {
            return kategoria_pojazdu;
        }

        public void setKategoria_pojazdu(String kategoria_pojazdu) {
            this.kategoria_pojazdu = kategoria_pojazdu;
        }

        public String getTyp() {
            return typ;
        }

        public void setTyp(String typ) {
            this.typ = typ;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getWariant() {
            return wariant;
        }

        public void setWariant(String wariant) {
            this.wariant = wariant;
        }

        public String getRodzajPojazdu() {
            return rodzajPojazdu;
        }

        public void setRodzajPojazdu(String rodzajPojazdu) {
            this.rodzajPojazdu = rodzajPojazdu;
        }

        public String getPochodzenie() {
            return pochodzenie;
        }

        public void setPochodzenie(String pochodzenie) {
            this.pochodzenie = pochodzenie;
        }

        public String getRokProdukcji() {
            return rokProdukcji;
        }

        public void setRokProdukcji(String rokProdukcji) {
            this.rokProdukcji = rokProdukcji;
        }

        public String getDataRejestracji() {
            return dataRejestracji;
        }

        public void setDataRejestracji(String dataRejestracji) {
            this.dataRejestracji = dataRejestracji;
        }

        public String getPojemnosc() {
            return pojemnosc;
        }

        public void setPojemnosc(String pojemnosc) {
            this.pojemnosc = pojemnosc;
        }

        public String getMasa() {
            return masa;
        }

        public void setMasa(String masa) {
            this.masa = masa;
        }

        public String getRodzajPaliwa() {
            return rodzajPaliwa;
        }

        public void setRodzajPaliwa(String rodzajPaliwa) {
            this.rodzajPaliwa = rodzajPaliwa;
        }
    }
}
