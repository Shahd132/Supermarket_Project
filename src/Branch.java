public class Branch 
{
    private int BranchId;
    private String Name;
    private String Location;
    private int AdminId;

    public Branch(int branchId, String name, String location, int AdminId) 
    {
        this.BranchId = branchId;
        this.Name = name;
        this.Location = location;
        this.AdminId = AdminId;
    }

    public void setManager(int adminId) 
    {
        this.AdminId = adminId;
    }

    public void setBranchId(int branchId) 
    {
        this.BranchId = branchId;
    }

    public void setName(String name) 
    {
        this.Name = name;
    }

    public void setLocation(String location) 
    {
        this.Location = location;
    }

    public String getLocation() 
    {
        return Location;
    }

    public int getManager() 
    {
        return AdminId;
    }

    public int getBranchId() 
    {
        return BranchId;
    }

    public String getName() 
    {
        return Name;
    }

    

}

