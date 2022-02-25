/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package healtop;

import java.awt.HeadlessException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author BANTOS BEN
 */
public class healtopClass {

    

    /**
     * @return the rsltAdmnId
     */
    public String getRsltAdmnId() {
        return rsltAdmnId;
    }

    /**
     * @return the rsltDocId
     */
    public String getRsltDocId() {
        return rsltDocId;
    }

    /**
     * @return the rsltRecId
     */
    public String getRsltRecId() {
        return rsltRecId;
    }

    /**
     * @return the rsltPatId
     */
    public String getRsltPatId() {
        return rsltPatId;
    }

    /**
     * @return the rsltBillId
     */
    public String getRsltBillId() {
        return rsltBillId;
    }

    private Connection conn;
    private Statement st;
    private PreparedStatement pat;
    private ResultSet rs;
    private String rsltAdmnId;
    private String rsltDocId;
    private String rsltRecId;
    private String rsltPatId;
    private String rsltBillId;

    public void connect() {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();
            JOptionPane.showMessageDialog(null, "Connected", "HealTop", JOptionPane.ERROR_MESSAGE);

            // rs.close();
            st.close();
            //pat.close();
        } catch (HeadlessException | SQLException e) {
            // JOptionPane.showMessageDialog(null,"Not Connected","HealTop",JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void DocDBInsert(String id, String fn, String sn, String nin, String eml, String adr, String pn, String age, String ms, String gen, String spec, String un, String pass, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "INSERT INTO `Doctors` "
                    + " (`DoctorId`,`FirstName`,`SecondName`,`Adress`,`Email`,`PhoneNumber`,`NationalIdentificationNumber`,`MaritalStatus`,`Age`,`Gender`,`BloodGroup`,`Specification`,`Username`,`Password`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, adr);
            pat.setString(5, eml);
            pat.setString(6, pn);
            pat.setString(7, nin);
            pat.setString(8, ms);
            pat.setString(9, age);
            pat.setString(10, gen);
            pat.setString(11, bg);
            pat.setString(12, spec);
            pat.setString(13, un);
            pat.setString(14, pass);

            pat.executeUpdate();

            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "New Doctor Added", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void ApntmDBInsert(String pid, String did, String ad) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "INSERT INTO `Appointments` "
                    + " (`PatientId`,`DoctorId`,`DateOfAppointment`) "
                    + "VALUES (?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, pid);
            pat.setString(2, did);
            pat.setString(3, ad);

            pat.executeUpdate();

            // rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "New Appointment added", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void BilltrDBInsert(String pid, String bid, String dop, String mop, String am) throws SQLException {
        try {
            String sql;
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            Calendar calendar = new GregorianCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String yr = Integer.toString(year);
            String mn = monthIndex(month);

            boolean rslt = false;
            sql = "SELECT `Year`,`Month` FROM `GenBills`";
            rs = st.executeQuery(sql);

            while (rs.next()) {

                if (yr.equals(rs.getString(1)) && mn.equals(rs.getString(2))) {
                    rslt = true;
                    break;
                }
            }

            sql = "INSERT INTO `BillTransactions` "
                    + " (`BillId`,`PatientId`,`ModeOfPayment`,`Amount`,`DateOfPayment`) "
                    + "VALUES (?,?,?,?,?)";
            pat = conn.prepareStatement(sql);

            if ("Cash".equals(mop)) {

                pat.setString(1, bid);
                pat.setString(2, pid);
                pat.setString(3, mop);
                pat.setString(4, am);
                pat.setString(5, dop);
                pat.executeUpdate();

                if (rslt == true) {
                    sql = "UPDATE `GenBills` "
                            + "SET `CashPayment`=[CashPayment]+'" + am + "'"
                            + "WHERE `Year`='" + yr + "' AND `Month`='" + mn + "'";
                    pat = conn.prepareStatement(sql);
                    pat.executeUpdate();
                    sql = "UPDATE `GenBills` "
                            + "SET `TotalPayment`=[ChequePayment]+[CashPayment]+'" + 0 + "' "
                            + "WHERE `Year`='" + yr + "' AND `Month`='" + mn + "'";
                    pat = conn.prepareStatement(sql);
                    pat.executeUpdate();

                } else {
                    sql = "INSERT INTO `GenBills` (`Year`,`Month`,`CashPayment`,`ChequePayment`,`TotalPayment`) VALUES(?,?,?,?,?)";
                    pat = conn.prepareStatement(sql);
                    pat.setString(1, yr);
                    pat.setString(2, mn);
                    pat.setString(3, am);
                    pat.setString(4, "0");
                    pat.setString(5, am);
                    pat.executeUpdate();

                }

                JOptionPane.showMessageDialog(null, "Cash Bill Transaction Successful", "HealTop", JOptionPane.INFORMATION_MESSAGE);
            } else {
                am = "0";
                pat.setString(1, bid);
                pat.setString(2, pid);
                pat.setString(3, mop);
                pat.setString(4, am);
                pat.setString(5, dop);
                pat.executeUpdate();
                JOptionPane.showMessageDialog(null, "Complete the next Part of Transaction", "HealTop", JOptionPane.INFORMATION_MESSAGE);

            }

            rs.close();
            st.close();
            pat.close();

        } catch (HeadlessException | SQLException e) {
            rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void ApprvChq(String cn) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            Calendar calendar = new GregorianCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String yr = Integer.toString(year);
            String mn = monthIndex(month);
            String sql;
            boolean rslt = false;
            sql = "SELECT `Year`,`Month` FROM `GenBills`";
            rs = st.executeQuery(sql);

            while (rs.next()) {

                if (yr.equals(rs.getString(1)) && mn.equals(rs.getString(2))) {
                    rslt = true;
                    break;
                }
            }
            rs.close();
            sql = "SELECT `BillId`,`Amount` FROM `UnapprovedCheque` WHERE `ChequeNo`='" + cn + "'";
            rs = st.executeQuery(sql);
            String am = rs.getString(2);
            String bi=rs.getString(1);

            sql = "DELETE FROM `UnapprovedCheque` WHERE `ChequeNo`='" + cn + "'";
            pat = conn.prepareStatement(sql);
            pat.executeUpdate();

                        sql = "UPDATE `BillTransactions`"
                                +" SET `Amount`='"+am+"' WHERE `BillId`='"+bi+"'";
                                pat = conn.prepareStatement(sql);
            if (rslt == true) {
                sql = "UPDATE `GenBills` "
                        + "SET `ChequePayment`=[ChequePayment]+'" + am + "'"
                        + "WHERE `Year`='" + yr + "' AND `Month`='" + mn + "'";
                pat = conn.prepareStatement(sql);
                pat.executeUpdate();
                sql = "UPDATE `GenBills` "
                        + "SET `TotalPayment`=[ChequePayment]+[CashPayment]+'" + 0 + "' "
                        + "WHERE `Year`='" + yr + "' AND `Month`='" + mn + "'";
                pat = conn.prepareStatement(sql);
                pat.executeUpdate();

            } else {

                sql = "INSERT INTO `GenBills` (`Year`,`Month`,`CashPayment`,`ChequePayment`,`TotalPayment`) VALUES(?,?,?,?,?)";
                pat = conn.prepareStatement(sql);
                pat.setString(1, yr);
                pat.setString(2, mn);
                pat.setString(3, "0");
                pat.setString(4, am);
                pat.setString(5, am);
                pat.executeUpdate();

            }
            rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "Cheque Bill Transaction Successful", "HealTop", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | SQLException e) {
            rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void UnpchDBInsert(String bid, String cn, String dd, String bn, String am) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "INSERT INTO `UnapprovedCheque` "
                    + " (`BillId`,`ChequeNo`,`DueDate`,`Bank`,`Amount`) "
                    + "VALUES (?,?,?,?,?)";
            pat = conn.prepareStatement(sql);

            pat.setString(1, bid);
            pat.setString(2, cn);
            pat.setString(3, dd);
            pat.setString(4, bn);
            pat.setString(5, am);

            pat.executeUpdate();

            // rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "Cheque Bill Transaction Pending \n For Cheque Approval by Administrator", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void PatDBInsert(String id, String fn, String sn, String nin, String pt, String adr, String pn, String age, String ms, String gen, String wn, String bn, String ad, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "INSERT INTO `Patients` "
                    + " (`PatientId`,`FirstName`,`SecondName`,`BC/NID No`,`Age`,`MaritalStatus`,`Adress`,`PhoneNumber`,`Gender`,`BloodGroup`,`PatientType`,`WardNumber`,`BedNumber`,`AdmissionDate`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, nin);
            pat.setString(5, age);
            pat.setString(6, ms);
            pat.setString(7, adr);
            pat.setString(8, pn);
            pat.setString(9, gen);
            pat.setString(10, bg);
            pat.setString(11, pt);
            pat.setString(12, wn);
            pat.setString(13, bn);
            pat.setString(14, ad);

            pat.executeUpdate();

            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "New Patient Added", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void PatDBUpdate(String id, String fn, String sn, String nin, String pt, String adr, String pn, String age, String ms, String gen, String wn, String bn, String ad, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Patients` WHERE `PatientId` = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();

            String sql = "INSERT INTO `Patients` "
                    + " (`PatientId`,`FirstName`,`SecondName`,`BC/NID No`,`Age`,`MaritalStatus`,`Adress`,`PhoneNumber`,`Gender`,`BloodGroup`,`PatientType`,`WardNumber`,`BedNumber`,`AdmissionDate`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, nin);
            pat.setString(5, age);
            pat.setString(6, ms);
            pat.setString(7, adr);
            pat.setString(8, pn);
            pat.setString(9, gen);
            pat.setString(10, bg);
            pat.setString(11, pt);
            pat.setString(12, wn);
            pat.setString(13, bn);
            pat.setString(14, ad);

            pat.executeUpdate();

            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "Patient Records Updated", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void DepatDBInsert(String id) throws SQLException {
        try {
            try {
                int days;
                String sql,NoDays;
                //Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:healtopdb.db";
                conn = DriverManager.getConnection(url);
                st = conn.createStatement();

                sql = "SELECT SUM(`Amount`) FROM `BillTransactions` WHERE `PatientId`='" + id + "'";
                rs = st.executeQuery(sql);
                String amn = rs.getString(1);
                int amnt;

                if (amn == null) {
                    amnt = 0;
                } else {
                    amnt = Integer.valueOf(amn);
                }

                sql = "SELECT `AdmissionDate`,`PatientType` FROM `Patients` WHERE `PatientId` = '" + id + "'";
                rs = st.executeQuery(sql);
                String dt = rs.getString(1);
                String pt = rs.getString(2);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date admission_date = sdf.parse(dt);
                admission_date.setYear(119);
                Date today = new Date();
               // System.out.println(today);
                //System.out.println(admission_date);


                days = getDateDiff(admission_date, today);
                NoDays=Integer.toString(days);
               // System.out.println(days);
                long total = days * 500;

                if (amnt >= total) {

                    boolean rslt = false;

                    sql = "SELECT `PatientId` FROM `Patients`";
                    rs = st.executeQuery(sql);
                    while (rs.next()) {

                        if (rs.getString(1).equals(id)) {
                            rslt = true;
                            break;
                        }
                    }

                    if (rslt) {
                        String fn, sn, nin, ad, rd, pn;
                        sql = "SELECT `FirstName`,`SecondName`,`BC/NID No`,`PhoneNumber`,`AdmissionDate` FROM `Patients` WHERE `PatientId` = ? ";
                        pat = conn.prepareStatement(sql);
                        pat.setString(1, id);
                        rs = pat.executeQuery();

                        fn = rs.getString(1);
                        sn = rs.getString(2);
                        nin = rs.getString(3);
                        pn = rs.getString(4);
                        ad = rs.getString(5);
                        LocalDate td = LocalDate.now();
                        rd = td.toString();

                        sql = "INSERT INTO `DischargedPatients` "
                                + " (`FirstName`,`SecondName`,`PhoneNo`,`BC/NID No`,`AdmissionDate`,`ReleasedDate`) "
                                + "VALUES (?,?,?,?,?,?)";
                        pat = conn.prepareStatement(sql);
                        pat.setString(1, fn);
                        pat.setString(2, sn);
                        pat.setString(3, pn);
                        pat.setString(4, nin);
                        pat.setString(5, ad);
                        pat.setString(6, rd);
                        //pat.setString(7,NoDays);

                        pat.executeUpdate();

                        sql = "DELETE FROM `Patients` WHERE `PatientId` = ?";
                        pat = conn.prepareStatement(sql);
                        pat.setString(1, id);
                        pat.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Patient " + id + " Discharged", "HealTop", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, id + " Not Found", "HealTop", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    int balance=(int) (total-amnt);
                    JOptionPane.showMessageDialog(null, "The Bill Not fully paid by the patient"+"\nArrears: Kshs."+balance, "HealTop", JOptionPane.ERROR_MESSAGE);
                }

            } catch (HeadlessException | SQLException | ParseException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            st.close();
            pat.close();
        } catch (SQLException ex) {
            rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void PatDBDelete(String id) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Patients` WHERE `PatientId` = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();

            JOptionPane.showMessageDialog(null, "Patients Record Deleted", "HealTop", JOptionPane.INFORMATION_MESSAGE);
            pat.close();
            st.close();
        } catch (SQLException ex) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void docPrflUpdate(String username, String password, String id) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "UPDATE `Doctors` "
                    + " SET `Username`=?,`Password`=? "
                    + "WHERE `DoctorId`=?";
            pat = conn.prepareStatement(sql);
            pat.setString(1, username);
            pat.setString(2, password);
            pat.setString(3, id);

            pat.executeUpdate();
            pat.close();
            st.close();
            JOptionPane.showMessageDialog(null, "Login Credentials Updated", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recPrflUpdate(String username, String password, String id) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "UPDATE `Receptionists` "
                    + " SET `Username`=?,`Password`=? "
                    + "WHERE `ReceptionistId`=?";
            pat = conn.prepareStatement(sql);
            pat.setString(1, username);
            pat.setString(2, password);
            pat.setString(3, id);

            pat.executeUpdate();
            pat.close();
            st.close();
            JOptionPane.showMessageDialog(null, "Login Credentials Updated", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void RecDBInsert(String id, String fn, String sn, String nin, String eml, String adr, String pn, String age, String ms, String gen, String un, String pass, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "INSERT INTO `Receptionists` "
                    + " (`ReceptionistId`,`FirstName`,`SecondName`,`NID No`,`Adress`,`Email`,`PhoneNumber`,`Age`,`MaritalStatus`,`Gender`,`BloodGroup`,`Username`,`Password`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, nin);
            pat.setString(5, adr);
            pat.setString(6, eml);
            pat.setString(7, pn);
            pat.setString(8, age);
            pat.setString(9, ms);
            pat.setString(10, gen);
            pat.setString(11, bg);
            pat.setString(12, un);
            pat.setString(13, pass);

            pat.executeUpdate();

            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "New Receptionist Added", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void RecDBUpdate(String id, String fn, String sn, String nin, String eml, String adr, String pn, String age, String ms, String gen, String un, String pass, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Receptionists` WHERE ReceptionistId = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();

            String sql = "INSERT INTO `Receptionists` "
                    + " (`ReceptionistId`,`FirstName`,`SecondName`,`NID No`,`Adress`,`Email`,`PhoneNumber`,`Age`,`MaritalStatus`,`Gender`,`BloodGroup`,`Username`,`Password`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, nin);
            pat.setString(5, adr);
            pat.setString(6, eml);
            pat.setString(7, pn);
            pat.setString(8, age);
            pat.setString(9, ms);
            pat.setString(10, gen);
            pat.setString(11, bg);
            pat.setString(12, un);
            pat.setString(13, pass);

            pat.executeUpdate();

            // rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "Receptionist Record Updated", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void DocDBUpdate(String id, String fn, String sn, String nin, String eml, String adr, String pn, String age, String ms, String gen, String spec, String un, String pass, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Doctors` WHERE DoctorId = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();

            String sql = "INSERT INTO `Doctors` "
                    + " (`DoctorId`,`FirstName`,`SecondName`,`Adress`,`Email`,`PhoneNumber`,`NationalIdentificationNumber`,`MaritalStatus`,`Age`,`Gender`,`BloodGroup`,`Specification`,`Username`,`Password`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, adr);
            pat.setString(5, eml);
            pat.setString(6, pn);
            pat.setString(7, nin);
            pat.setString(8, ms);
            pat.setString(9, age);
            pat.setString(10, gen);
            pat.setString(11, bg);
            pat.setString(12, spec);
            pat.setString(13, un);
            pat.setString(14, pass);

            pat.executeUpdate();

            // rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "Doctor Record Updated", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void AdmnDBDelete(String id) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Administrators` WHERE `AdminId` = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();

            JOptionPane.showMessageDialog(null, "Account Deleted", "HealTop", JOptionPane.INFORMATION_MESSAGE);

            st.close();
            pat.close();
        } catch (SQLException ex) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void AdmnDBUpdate(String id, String fn, String sn, String nin, String eml, String adr, String pn, String age, String ms, String gen, String un, String pass, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Administrators` WHERE `AdminId` = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();

            String sql = "INSERT INTO `Administrators` "
                    + " (`AdminId`,`FirstName`,`SecondName`,`NID No`,`Adress`,`Email`,`PhoneNo`,`Age`,`MaritalStatus`,`Gender`,`BloodGroup`,`Username`,`Password`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, nin);
            pat.setString(5, adr);
            pat.setString(6, eml);
            pat.setString(7, pn);
            pat.setString(8, age);
            pat.setString(9, ms);
            pat.setString(10, gen);
            pat.setString(11, bg);
            pat.setString(12, un);
            pat.setString(13, pass);

            pat.executeUpdate();

            // rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "Profile Updated", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public void AdmnDBInsert(String id, String fn, String sn, String nin, String eml, String adr, String pn, String age, String ms, String gen, String un, String pass, String bg) throws SQLException {
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "INSERT INTO `Administrators` "
                    + " (`AdminId`,`FirstName`,`SecondName`,`NID No`,`Adress`,`Email`,`PhoneNo`,`Age`,`MaritalStatus`,`Gender`,`BloodGroup`,`Username`,`Password`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pat = conn.prepareStatement(sql);
            pat.setString(1, id);
            pat.setString(2, fn);
            pat.setString(3, sn);
            pat.setString(4, nin);
            pat.setString(5, adr);
            pat.setString(6, eml);
            pat.setString(7, pn);
            pat.setString(8, age);
            pat.setString(9, ms);
            pat.setString(10, gen);
            pat.setString(11, bg);
            pat.setString(12, un);
            pat.setString(13, pass);

            pat.executeUpdate();

            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, "New Admin Added", "HealTop", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, e.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        //return st;

    }

    public int Doclogin(String username, String password) throws SQLException {
        int log = 0;
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT Username, Password FROM `Doctors`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                    log = 1;
                    break;
                }
            }
            rs.close();
            st.close();
            // pat.close();
        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return log;

    }

    public int Admnlogin(String username, String password) throws SQLException {
        int log = 0;
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `Username`, `Password` FROM `Administrators`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                    log = 1;
                    break;
                }
            }
            rs.close();
            st.close();
            // pat.close();
        } catch (SQLException ex) {
            rs.close();
            st.close();
            //pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return log;

    }

    public void canclapp(String ad) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();


                String sql = "DELETE FROM `Appointments` WHERE `DateOfAppointment`='" + ad + "'";
                pat = conn.prepareStatement(sql);
                pat.executeUpdate();

            rs.close();
            st.close();
            pat.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int Reclogin(String username, String password) throws SQLException {
        int log = 0;
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT Username, Password FROM `Receptionists`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                    log = 1;
                    break;
                }
            }
            rs.close();
            st.close();
            // pat.close();
        } catch (SQLException ex) {
            rs.close();
            st.close();
            //pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return log;

    }

    public void delDocDb(String id) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Doctors` WHERE DoctorId = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record Deleted", "HealTop", JOptionPane.INFORMATION_MESSAGE);

            pat.close();
            st.close();
        } catch (SQLException ex) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void delRecDb(String id) throws SQLException {
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String delSql = "DELETE FROM `Receptionists` WHERE ReceptionistId = ?";
            pat = conn.prepareStatement(delSql);
            pat.setString(1, id);
            pat.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record Deleted", "HealTop", JOptionPane.INFORMATION_MESSAGE);

            pat.close();
            st.close();
        } catch (SQLException ex) {
            //rs.close();
            st.close();
            pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String EncryptPassword(String password) {
        String EncryptedPassword="";
        char[] passwordCharacter;
        int PasswordLength, AsciiVal;
        int passSum = 0, salt = 33;
        passwordCharacter = password.toCharArray();
        PasswordLength = passwordCharacter.length - 1;
        for (int i = PasswordLength; i >= 0; i--) {
            AsciiVal = passwordCharacter[i];
            passSum += AsciiVal + 3;
            if (i % 2 == 0) {
                passSum += salt;
                ++salt;
            }
            EncryptedPassword += Integer.toHexString(passSum);
            if (EncryptedPassword.length() >= 16) {
                break;
            }
        }
        int chk;
        passwordCharacter = EncryptedPassword.toCharArray();
        PasswordLength = passwordCharacter.length - 1;
        chk = PasswordLength;
        while (chk < 16) {
            for (int i = PasswordLength; i >= 0; i--) {
                AsciiVal = passwordCharacter[i];
                passSum += AsciiVal + 3;
                if (i % 2 == 0) {
                    passSum += salt;
                    ++salt;
                }
                EncryptedPassword += Integer.toHexString(passSum);
                if (EncryptedPassword.length() > 16) {
                    break;
                }
            }
            passwordCharacter = EncryptedPassword.toCharArray();
            PasswordLength = passwordCharacter.length - 1;
            chk = PasswordLength;
        }
        return EncryptedPassword;
    }

    public String genAdmnId() {
        String Id;
        Random rand = new Random();
        Id = "A" + rand.nextInt(1000000);
        //System.out.println(Id);
        return Id;
    }

    public boolean AdmnId() throws SQLException {
        boolean rslt = true;
        try {
            String Id = genAdmnId();
            rsltAdmnId = Id;
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `AdminId` FROM `Administrators`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(Id)) {
                    rslt = false;
                    break;
                }
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    public String RtnAdminId(String username, String password) throws SQLException {
        String rslt = null;
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `AdminId` FROM `Administrators` WHERE `Username`='" + username + "' AND `Password`='" + password + "'";
            rs = st.executeQuery(sql);
            rslt = rs.getString(1);

            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    public String RtnDocId(String username, String password) throws SQLException {
        String rslt = null;
        try {
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `DoctorId` FROM `Doctors` WHERE `Username`='" + username + "' AND `Password`='" + password + "'";
            rs = st.executeQuery(sql);
            rslt = rs.getString(1);

            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    public String RtnRecId(String username, String password) throws SQLException {
        String rslt = null;
        try {

            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `ReceptionistId` FROM `Receptionists` WHERE `Username`='" + username + "' AND `Password`='" + password + "'";
            rs = st.executeQuery(sql);
            rslt = rs.getString(1);

            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            //pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    private String genDocId() {
        String Id;

        Random rand = new Random();
        Id = "D" + rand.nextInt(1000000);
        return Id;
    }

    public boolean DocId() throws SQLException {
        boolean rslt = true;
        try {
            String Id = genDocId();
            rsltDocId = Id;
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `DoctorId` FROM `Doctors`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(Id)) {
                    rslt = false;
                    break;
                }
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    private String genPatId() {
        String Id;
        int chk;
        Random rand = new Random();
        chk = rand.nextInt();
        while (chk <= 100) {
            chk = rand.nextInt();
        }
        Id = "P" + chk;
        // System.out.println(Id);
        return Id;
    }

    public boolean PatId() throws SQLException {
        boolean rslt = true;
        try {
            String Id = genPatId();
            rsltPatId = Id;
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `PatientId` FROM `Patients`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(Id)) {
                    rslt = false;
                    break;
                }
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    private String genRecId() {
        String Id;
        Random rand = new Random();
        Id = "R" + rand.nextInt(1000000);
        // System.out.println(Id);
        return Id;
    }

    public boolean RecId() throws SQLException {
        boolean rslt = true;
        try {
            String Id = genRecId();
            rsltRecId = Id;
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `ReceptionistId` FROM `Receptionists`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(Id)) {
                    rslt = false;
                    break;
                }
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            // pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    public String MedId() {
        String Id;
        int chk;
        Random rand = new Random();
        chk = rand.nextInt();
        while (chk <= 100) {
            chk = rand.nextInt();
        }
        Id = "M" + chk;
        // System.out.println(Id);
        return Id;
    }

    private String genBillId() {
        String Id;
        int chk;
        Random rand = new Random();
        chk = rand.nextInt();
        while (chk <= 100) {
            chk = rand.nextInt();
        }
        Id = "B" + chk;
        //  System.out.println(Id);
        return Id;
    }

    public boolean BillId() throws SQLException {
        boolean rslt = true;
        try {
            String Id = genBillId();
            rsltBillId = Id;
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();

            String sql = "SELECT `BillId` FROM `BillTransactions`";
            rs = st.executeQuery(sql);
            while (rs.next()) {

                if (rs.getString(1).equals(Id)) {
                    rslt = false;
                    break;
                }
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            rs.close();
            st.close();
            //pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return rslt;
    }

    private String monthIndex(int index) {
        String month = null;

        switch (index) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;

        }

        return month;
    }

    public boolean contactchk(String contact) {
        boolean rslt = false;

        int con = Integer.valueOf(contact);
        if (con <= 799999999 && con >= 700000000) {
            rslt = true;
        }

        return rslt;
    }

    private int getDateDiff(Date dateAdmitted, Date dateCurrent) {
        int days = 0;
        long day;
        day=dateCurrent.getTime() - dateAdmitted.getTime();
        days=(int) (day/(24*60*60*1000));
        
        return days;

    }

    public boolean patientexist(String id) throws SQLException {
        boolean pexist=false;
        try{
        
            String url = "jdbc:sqlite:healtopdb.db";
            conn = DriverManager.getConnection(url);
            st = conn.createStatement();
            
            String sql = "SELECT * FROM `Patients` WHERE `PatientId` = '"+id+"'";
            rs = st.executeQuery(sql);
            if(rs.next()){
                pexist=true;
            }
        
                } catch (SQLException ex) {
            rs.close();
            st.close();
            //pat.close();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HealTop", JOptionPane.ERROR_MESSAGE);
        }
        return pexist;
    }

}
