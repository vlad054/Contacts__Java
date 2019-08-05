package contacts;

import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {


        Application app = null;
        ObjectOutputStream oout=null;

        //file job
        if (args.length!=0) {

            File file = new File(args[0]);
            if (file.exists()) {
                try {
                    FileInputStream filein = new FileInputStream(file);
                    ObjectInputStream oin = new ObjectInputStream(filein);
                    app = (Application) oin.readObject();
                    //oin.close();
                    //filein.close();
                    FileOutputStream fileOut = new FileOutputStream(file);
                    oout = new ObjectOutputStream(fileOut);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    FileOutputStream fileOut = new FileOutputStream(args[0]);
                    oout = new ObjectOutputStream(fileOut);
                    app = new Application();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        else{
            app = new Application();
        }


        Scanner scn = new Scanner(System.in);

        String act;

        do{
            System.out.println("[menu] Enter action (add, list, search, count, exit): ");
            act = scn.nextLine();
            switch (act) {
                case "add":{
                    app.addContact();
                    System.out.println();

                    if (oout!=null){
                        try{
                            oout.writeObject(app);
                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                    }

                    break;
                }
                case "list":{
                    app.getContactsList();
                    System.out.println();
                    System.out.println("[list] Enter action ([number], back): ");
                    String str = scn.nextLine();
                    int recNum;
                    if(str.equals("back")){
                        break;
                    }
                    else{
                        try{
                            recNum = Integer.parseInt(str);
                            app.getContactInfo(recNum);
                        }
                        catch(Exception e){
                            System.out.println("Wrong action!");
                            break;
                        }
                    }
                    System.out.println();
                    String actInt;
                    do {
                        System.out.println("[record] Enter action (edit, delete, menu): ");
                        actInt = scn.nextLine();
                        if (actInt.equals("menu")) {
                            break;
                        } else if (actInt.equals("edit")) {

                            app.editContact(recNum-1);
                            System.out.println();
                            if (oout != null) {
                                try {
                                    oout.writeObject(app);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            //System.out.println("[record] Enter action (edit, delete, menu): ");

                        } else if (actInt.equals("delete")) {
                            app.removeContact(recNum-1);
                            System.out.println();
                            if (oout != null) {
                                try {
                                    oout.writeObject(app);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                    }
                    while (!actInt.equals("menu"));
                    System.out.println();
                    break;
                }
                case "count" : {
                    System.out.println("The Phone Book has " + app.countContacts() +" records.");
                    System.out.println();
                    break;
                }
                case "search":{
                    List<Integer> lCnt = app.search();
                    System.out.println();
                    System.out.println("[search] Enter action ([number], back, again): ");
                    String strInt;
                    do{
                        strInt = scn.nextLine();
                        if(strInt.equals("back")){
                            break;
                        }
                        else if(strInt.equals("again")){
                            lCnt = app.search();
                            System.out.println();
                            System.out.println("[search] Enter action ([number], back, again): ");
                            continue;
                        }
                        else {
                            int recNum = Integer.parseInt(strInt);
                            app.getContactInfo(recNum);
                            System.out.println();
                            System.out.println("[record] Enter action (edit, delete, menu): ");
                            String actInt;
                            do {
                                actInt = scn.nextLine();
                                if (actInt.equals("menu")) {
                                    strInt = "back";
                                    break;
                                } else if (actInt.equals("edit")) {

                                    app.editContact(lCnt.get(recNum-1));
                                    System.out.println();
                                    if (oout != null) {
                                        try {
                                            oout.writeObject(app);
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    System.out.println("[record] Enter action (edit, delete, menu): ");

                                } else if (actInt.equals("delete")) {
                                    app.removeContact(lCnt.get(recNum-1));
                                    System.out.println();
                                    if (oout != null) {
                                        try {
                                            oout.writeObject(app);
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                    System.out.println("[record] Enter action (edit, delete, menu): ");
                                }
                            }
                            while (!actInt.equals("menu"));


                        }

                    }
                    while(!strInt.equals("back"));
                    System.out.println();
                    break;
                }
            }

        }
        while (!act.equals("exit"));

    }
}
class Application implements Serializable {
    private ArrayList<Contact> listContacts = new ArrayList<>();


    public int countContacts(){
        return this.listContacts.size();
    }

    public void  getContactInfo(int recNum) {
        if (this.listContacts.size() == 0) {
            System.out.println("No records to list!");
        } else {
            Contact cnt = listContacts.get(recNum - 1);
            for (String str : cnt.allFieldsForChange().keySet()) {
                System.out.println(cnt.allFieldsForChange().get(str) + ": " + cnt.getFieldVal(str));
            }
            System.out.println("Time created: "+cnt.getTimeCreated().withNano(0).withSecond(0));
            System.out.println("Time last edit: "+cnt.getTimeModified().withNano(0).withSecond(0));

        }
    }

    public void editContact(int recNum){
        if(this.listContacts.size()==0) {
            System.out.println("No records to edit!");
        }
        else{
            Scanner scn = new Scanner(System.in);

            Contact cnt =listContacts.get(recNum);

            Object[] strShName = cnt.allFieldsForChange().keySet().toArray();
            System.out.println("Select a field ("+ Arrays.toString(strShName).replace("[","").replace("]","")+")");

            String field = scn.nextLine();

            System.out.println("Enter "+cnt.allFieldsForChange().get(field).toLowerCase()+" :");

            cnt.setFieldVal(field,scn.nextLine());

            System.out.println("Saved");
            this.getContactInfo(recNum+1);
        }
    }

    public void removeContact(int recNum){
        if(this.listContacts.size()==0) {
            System.out.println("No records to remove!");
        }
        else{
            listContacts.remove(recNum);
            System.out.println("The record removed!");
        }
    }



    public void getContactsList(){
        if(this.listContacts.size()==0) {
            System.out.println("No records to list");
        }
        else{
            int i=1;

            for (Contact c:listContacts){
                System.out.println(i + ". " + c.getFullName());
                i++;
            }
        }
    }


    public void addContact(){

        Scanner scn = new Scanner(System.in);

        System.out.println("Enter the type (person, organization): ");


        switch (scn.nextLine()){
            case "person" :{
                PersonContact cont = new PersonContact();

                System.out.println("Enter the name: ");
                cont.setName(scn.nextLine());
                System.out.println("Enter the surname: ");
                cont.setSurname(scn.nextLine());
                System.out.println("Enter the birth date: ");
                cont.setBirthDate(scn.nextLine());
                System.out.println("Enter the gender (M, F): ");
                cont.setGender(scn.nextLine());
                System.out.println("Enter the number:");
                String ss = scn.nextLine();
                cont.setPhone(ss);
                System.out.println("A record added.");
                listContacts.add(cont);
                break;
            }
            case "organization":{
                OrganisationContact cont = new OrganisationContact();

                System.out.println("Enter the organization name: ");
                cont.setName(scn.nextLine());
                System.out.println("Enter the address: ");
                cont.setAddress(scn.nextLine());
                System.out.print("Enter the number:");
                cont.setPhone(scn.nextLine());
                System.out.println("A record added.");
                listContacts.add(cont);
                break;
            }
            default:{
                System.out.println("Bad type!");
            }
        }

    }
    List<Integer> search(){

        Scanner scn = new Scanner(System.in);
        System.out.println("Enter search query: ");
        String strSearch = scn.nextLine();
        Pattern pat = Pattern.compile(strSearch.toLowerCase());

        List<Integer> lInt = new ArrayList<>();

        for(Contact cnt:listContacts){
            for (String str : cnt.allFieldsForChange().keySet()) {
                Matcher mat = pat.matcher(cnt.getFieldVal(str).toLowerCase());

                if(mat.find()){
                    lInt.add(listContacts.indexOf(cnt));
                    break;
                }
            }
        }
        System.out.println("Found "+lInt.size()+" results:");
        int k=1;
        for(int i:lInt){
            System.out.println(k+". "+listContacts.get(i).getFullName());
            k++;

        }
        return lInt;

    }
}


abstract class Contact implements Serializable{

    private String name;
    private String phone;

    protected void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    protected void setTimeModified(LocalDateTime timeModified) {
        this.timeModified = timeModified;
    }

    private LocalDateTime timeCreated;
    private LocalDateTime timeModified;

    private boolean isPerson;


    public Contact(){
        this.name = "";
        this.phone = "";
        this.timeCreated = LocalDateTime.now();
        this.timeModified = null;
    }

    public Contact(String name, String phone){
        this.name = name;
        this.phone = phone;
        this.timeCreated = LocalDateTime.now();
        this.timeModified = null;

    }

    public String getName() { return name;}

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {

        this.name = name;
        this.timeModified = LocalDateTime.now();
    }

    public void setPhone(String phone) {

        if (isValidNumber(phone)){
            this.phone = phone;
            this.timeModified = LocalDateTime.now();
        }
        else{
            this.phone = "[no number]";
            System.out.println("Wrong number format!");
            this.timeModified = LocalDateTime.now();
        }
    }
    public boolean hasNumber(){
        return !this.phone.equals("");
    }

    private boolean isValidNumber(String str){

        Pattern pat = Pattern.compile("[+]?\\w?[\\s-]?(\\([\\w]{2,}\\)|" +
                "[\\w]{2,}[\\s-]\\([\\w]{2,}\\)|" +
                "[\\w]{2,})([\\s-][\\w]{2,})*");
        Matcher mat = pat.matcher(str);
        return mat.matches();
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public LocalDateTime getTimeModified() {
        return timeModified;
    }

    abstract Map<String,String> allFieldsForChange();
    abstract void setFieldVal(String fName, String newVal);
    abstract String getFieldVal(String fName);
    abstract String getFullName();


}

class OrganisationContact extends Contact{

    public OrganisationContact(String name, String address, String phone){
        super(name,phone);
        this.address = address;
    }

    public OrganisationContact(){
        super();
        this.address = "[no data]";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {

        if(address != ""){
            this.address = address;
            super.setTimeModified(LocalDateTime.now());
        }
        else{
            System.out.println("Bad address!");
        }
    }

    private String address;

    public Map<String,String> allFieldsForChange(){
        Map<String,String> mapStr=new LinkedHashMap<>();
        mapStr.put("name","Organization name");
        mapStr.put("address","Address");
        mapStr.put("number", "Number");
        return mapStr;
    }

    public void setFieldVal(String fName, String newVal){
        switch (fName){
            case "name":{
                this.setName(newVal);
                break;
            }
            case "address":{
                this.setAddress(newVal);
                break;
            }
            case "number":{
                this.setPhone(newVal);
                break;
            }
            default:
                System.out.println("Bad field name!");
        }
    }

    public String getFieldVal(String fName){
        switch (fName){
            case "name":{
                return getName();
            }
            case "address":{
                return getAddress();
            }
            case "number":{
                return getPhone();
            }
            default:
                System.out.println("Bad field name!");
                return "";
        }
    }
    public String getFullName(){
        return getName();
    }

}

class PersonContact extends Contact{

    public PersonContact(String name, String surname, String phone,String bDate, char gender){
        super(name,phone);
        this.surname = surname;
        this.birthDate = LocalDate.parse(bDate);
        this.gender = gender;
    }

    public PersonContact(){
        super();
        this.surname = "";
        this.birthDate = null;
        this.gender = Character.MIN_VALUE;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {

        this.surname = surname;
        super.setTimeModified(LocalDateTime.now());
    }

    public String getBirthDate() {
        if(birthDate == null){
            return "[no data]";
        }
        else{
            return birthDate.toString();
        }
    }

    public void setBirthDate(String birthDate) {

        try{
            LocalDate ld = LocalDate.parse(birthDate);
            this.birthDate = ld;
            super.setTimeModified(LocalDateTime.now());

        }
        catch (Exception e){
            System.out.println("Bad birth date!");
        }
    }

    public String getGender() {

        if(this.gender==Character.MIN_VALUE){
            return "[no data]";
        }
        else {
            return String.valueOf(this.gender);
            /*if(this.gender=='M'){
                return "Male";
            }
            else return "Female";*/
        }
    }

    public void setGender(String gen) {
        if (gen.length() == 1 && (gen.charAt(0) == 'M' || gen.charAt(0) == 'F')) {
            this.gender = gen.charAt(0);
            super.setTimeModified(LocalDateTime.now());
        } else {
            System.out.println("Bad gender!");
        }
    }

    public Map<String,String> allFieldsForChange(){
        Map<String,String> mapStr=new LinkedHashMap<>();
        mapStr.put("name","Name");
        mapStr.put("surname","Surname");
        mapStr.put("birth","Birth date");
        mapStr.put("gender","Gender");
        mapStr.put("number", "Number");
        return mapStr;
    }

    public void setFieldVal(String fName, String newVal){
        switch (fName){
            case "name":{
                this.setName(newVal);
                break;
            }
            case "surname":{
                this.setSurname(newVal);
                break;
            }
            case "number":{
                this.setPhone(newVal);
                break;
            }
            case "birth":{
                this.setBirthDate(newVal);
                break;
            }
            case "gender":{
                this.setGender(newVal);
                break;
            }
            default:
                System.out.println("Bad field name!");
        }
    }

    public String getFieldVal(String fName){
        switch (fName){
            case "name":{
                return getName();
            }
            case "surname":{
                return getSurname();
            }
            case "number":{
                return getPhone();
            }
            case "birth":{
                return getBirthDate();
            }
            case "gender":{
                return getGender();
            }
            default:
                System.out.println("Bad field name!");
                return "";
        }
    }
    public String getFullName(){
        return getName()+" "+getSurname();
    }


    private LocalDate birthDate;
    private char gender;
    private String surname;

}
