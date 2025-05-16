public class Category 
{
    private int CategoryId;
    private String Name;
    private String Description;

    public Category(int categoryId, String name, String description) 
    {
        this.CategoryId = categoryId;
        this.Name = name;
        this.Description = description;
    }

    public String getDescription() 
    {
        return Description;
    }

    public int getCategoryId() 
    {
        return CategoryId;
    }

    public String getName() 
    {
        return Name;
    }

    public void setCategoryId(int categoryId) 
    {
        this.CategoryId = categoryId;
    }

    public void setName(String name) 
    {
        this.Name = name;
    }

    public void setDescription(String description) 
    {
        this.Description = description;
    }

}

