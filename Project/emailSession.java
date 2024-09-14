package Project;

public class emailSession {
    private static emailSession instance;
    private String email;

    public static emailSession getInstance(){
        if(instance == null){
            instance = new emailSession();}
        return instance;}

    public void setEmail(String email){
        this.email = email;}

    public String getEmail(){
        return email;}}
