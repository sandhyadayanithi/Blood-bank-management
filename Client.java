import java.util.Scanner;

public class Client{
  private String name;
  private String location;
  private String bloodType;
  private double quantity;
  private int urgency;
  public String status;

  public Client(){
    this.status="Pending";
  }

  public void getDetails(){
    Scanner sc=new Scanner(System.in);
    System.out.printf("Enter the your details:\n");
    System.out.printf("Enter your name:");
    this.name=sc.nextLine();
    System.out.println("Enter you location(city):");
    this.location=sc.nextLine();
    System.out.printf("Enter the blood type needed:");
    this.bloodType=sc.nextLine();
    System.out.printf("Enter the quantity of blood needed in units(1 unit=450ml):");
    this.quantity=sc.nextDouble();
    System.out.printf("Enter the no. of days within which the blood is required:");
    this.urgency=sc.nextInt();
    sc.close();
    System.out.println("New client details added successfully.");
  }
}