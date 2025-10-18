import java.util.*;

public class Bank {
  private int ID;
  private String name;
  private String location;
  private ArrayList<Blood> bloodType;

  //for adding new bank
  public void getDetails(){
    Scanner sc=new Scanner(System.in);
    System.out.println("Enter the bank details:");
    System.out.printf("Enter the bank ID:");
    this.ID=sc.nextInt();
    System.out.println("Enter the name of the bank:");
    this.name=sc.nextLine();
    System.out.println("Enter the location of the bank(city):");
    this.location=sc.nextLine();
    System.out.println("Enter the no. of blood types available:");
    int n=sc.nextInt();
    for(int i=0;i<n;i++){
      Blood blood=new Blood();
      System.out.println("Enter the blood type:");
      blood.type=sc.nextLine();
      System.out.println("Enter the quantity available of the blood type:");
      blood.availableQty=sc.nextDouble();
      bloodType.add(blood);
    }
    sc.close();
    System.out.println("New bank details added successfully.");
  }
}
