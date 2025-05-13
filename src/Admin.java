public class Admin 
{
    private int AdminId;
    private String Name;
    private String Email;
    private String Password;

    public Admin() {}
    public Admin(String Name, String Email, String Password) 
    {
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
    }
    public int getId()
    {
        return AdminId;
    }
    public String getName()
    {
        return Name;
    }
    public String getEmail()
    {
        return Email;
    }
    public String getPassword()
    {
        return Password;
    }
   
    public void setName(String name)
    {
        this.Name=name;
    }
    public void setEmail(String email)
    {
        this.Email=email;
    }
    public void setPassword(String password)
    {
        this.Password=password;
    }
}
