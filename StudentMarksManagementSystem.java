import java.sql.*;
import java.util.Scanner;
class StudentMarksManagementSystem {
static final String JDBC_URL = "jdbc:mysql://localhost:3306/studentsmarks";
static final String USER = "root";
static final String PASSWORD = "Wb@758004nak";
static Connection connection;
static Statement statement;
static ResultSet resultSet;
public static void main(String[] args) {
try{
connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
statement = connection.createStatement();
Student student = new Student(connection);
Teacher teacher = new Teacher(connection);
Scanner scanner = new Scanner(System.in);
int choice;
do {
System.out.println("===== Student Marks Management System =====");
System.out.println("1. Student Menu");
System.out.println("2. Teacher Menu");
System.out.println("0. Exit");
System.out.print("Enter your choice: ");
choice = scanner.nextInt();
switch (choice) {
case 1:
student.studentMenu(scanner);
break;
case 2:
teacher.teacherMenu(scanner);
break;
case 0:
System.out.println("Exiting the program. Goodbye!");
break;
default:
System.out.println("Invalid choice. Please try again.");
}
} while (choice != 0);
connection.close();
scanner.close();
} catch (SQLException e) {
e.printStackTrace();
}
}
}
class Student {
private Connection connection;
public Student(Connection connection) {
this.connection = connection;
}
public void studentMenu(Scanner scanner) {
int choice;
do {
System.out.println("===== Student Menu =====");
System.out.println("1. Update Credentials");
System.out.println("2. Add Student");
System.out.println("0. Back to Main Menu");
System.out.print("Enter your choice: ");
choice = scanner.nextInt();
switch (choice) {
case 1:
updateCredentials(scanner);
break;
case 2:
addStudent(scanner);
break;
case 0:
System.out.println("Returning to main menu.");
break;
default:
System.out.println("Invalid choice. Please try again.");
}
} while (choice != 0);
}
private void updateCredentials(Scanner scanner) {
System.out.print("Enter student ID: ");
int studentID = scanner.nextInt();
System.out.print("Enter new student name: ");
scanner.nextLine(); 
String newName = scanner.nextLine();
try {
PreparedStatement preparedStatement = connection.prepareStatement(
"UPDATE Student SET StudentName = ? WHERE StudentID = ?");
preparedStatement.setString(1, newName);
preparedStatement.setInt(2, studentID);
int rowsAffected = preparedStatement.executeUpdate();
if (rowsAffected > 0) {
System.out.println("Student credentials updated successfully!");
} else {
System.out.println("Student not found with the given ID.");
}
} catch (SQLException e) {
e.printStackTrace();
}
}
private void addStudent(Scanner scanner) {
System.out.print("Enter student id: ");
int id=scanner.nextInt();
System.out.print("Enter student name: ");
String studentName = scanner.next();
System.out.print("Enter subject code: ");
int subjectCode = scanner.nextInt();
System.out.print("Enter teacher code: ");
int teacherCode = scanner.nextInt();
try {
PreparedStatement preparedStatement = connection.prepareStatement(
"INSERT INTO Student (StudentID,StudentName, SubjectCode, TeacherID) VALUES (?,?, ?, ?)");
preparedStatement.setInt(1, id);
preparedStatement.setString(2, studentName);
preparedStatement.setInt(3, subjectCode);
preparedStatement.setInt(4, teacherCode);
int rowsAffected = preparedStatement.executeUpdate();
if (rowsAffected > 0) {
System.out.println("Student added successfully!");
} else {
System.out.println("Failed to add student.");
}
} catch (SQLException e) {
e.printStackTrace();
}
}
}
class Teacher {
private Connection connection;
public Teacher(Connection connection) {
this.connection = connection;
}
public void teacherMenu(Scanner scanner) {
int choice;
do {
System.out.println("===== Teacher Menu =====");
System.out.println("1. Show Student Details");
System.out.println("2. Update Student Marks");
System.out.println("3. Show Marks of All Students in Every Subject");
System.out.println("0. Back to Main Menu");
System.out.print("Enter your choice: ");
choice = scanner.nextInt();
switch (choice) {
case 1:
showStudentDetails(scanner);
break;
case 2:
updateStudentMarks(scanner);
break;
case 3:
showMarksOfAllStudents();
break;
case 0:
System.out.println("Returning to main menu.");
break;
default:
System.out.println("Invalid choice. Please try again.");
}
} while (choice != 0);
}
private void showStudentDetails(Scanner scanner) {
System.out.print("Enter student ID: ");
int studentID = scanner.nextInt();
try {
PreparedStatement preparedStatement = connection.prepareStatement(
"SELECT * FROM Student WHERE StudentID = ?");
preparedStatement.setInt(1, studentID);
ResultSet resultSet = preparedStatement.executeQuery();
if (resultSet.next()) {
System.out.println("===== Student Details =====");
System.out.println("Student ID: " + resultSet.getInt("StudentID"));
System.out.println("Student Name: " + resultSet.getString("StudentName"));
System.out.println("Subject Code: " + resultSet.getInt("SubjectCode"));
System.out.println("Teacher Code: " + resultSet.getInt("TeacherID"));
} else {
System.out.println("Student not found with the given ID.");
}
} catch (SQLException e) {
e.printStackTrace();
}
}
private void updateStudentMarks(Scanner scanner) {
System.out.print("Enter your Teacher ID: ");
int teacherID = scanner.nextInt();
if (!validateTeacherID(teacherID)) {
System.out.println("Invalid Teacher ID. You cannot update student marks.");
return;
}
System.out.print("Enter student ID: ");
int studentID = scanner.nextInt();
try {
PreparedStatement fetchMarksStatement = connection.prepareStatement(
"SELECT Student.StudentID, Subject.SubjectCode " +
"FROM Student " +
"INNER JOIN Subject ON Student.SubjectCode = Subject.SubjectCode " +
"WHERE StudentID = ?");
fetchMarksStatement.setInt(1, studentID);
ResultSet resultSet = fetchMarksStatement.executeQuery();
if (resultSet.next()) {
int subjectCode = resultSet.getInt("SubjectCode");
if (!validateTeacherSubject(teacherID, subjectCode)) {
System.out.println("You are not assigned to the subject. Cannot update marks.");
return;
}
System.out.println("===== Current Marks =====");
System.out.println("Subject Code: " + subjectCode);
System.out.print("Are you sure about your changes? (y/n): ");
char confirmation = scanner.next().charAt(0);
if (confirmation == 'y' || confirmation == 'Y') {
System.out.print("Enter new mark: ");
int newMark = scanner.nextInt();
PreparedStatement updateMarksStatement = connection.prepareStatement(
"UPDATE Marks SET Mark = ? WHERE StudentID = ? AND SubjectCode = ?");
updateMarksStatement.setInt(1, newMark);
updateMarksStatement.setInt(2, studentID);
updateMarksStatement.setInt(3, subjectCode);
int rowsAffected = updateMarksStatement.executeUpdate();
if (rowsAffected > 0) {
System.out.println("Marks updated successfully!");
} else {
System.out.println("Failed to update marks.");
}
} else {
System.out.println("Marks not updated.");
}
} else {
System.out.println("Student not found with the given ID.");
}
} catch (SQLException e) {
e.printStackTrace();
}
}
private boolean validateTeacherID(int teacherID) {
try {
PreparedStatement preparedStatement = connection.prepareStatement(
"SELECT * FROM Teacher WHERE TeacherID = ?");
preparedStatement.setInt(1, teacherID);
ResultSet resultSet = preparedStatement.executeQuery();
return resultSet.next(); 
} catch (SQLException e) {
e.printStackTrace();
return false;
}
}
private boolean validateTeacherSubject(int teacherID, int subjectCode) {
try {
PreparedStatement preparedStatement = connection.prepareStatement(
"SELECT * FROM Teacher WHERE TeacherID = ? AND SubjectCode = ?");
preparedStatement.setInt(1, teacherID);
preparedStatement.setInt(2, subjectCode);
ResultSet resultSet = preparedStatement.executeQuery();
return resultSet.next(); 
} catch (SQLException e) {
e.printStackTrace();
return false;
}
}
private void showMarksOfAllStudents() {
try {
Statement statement = connection.createStatement();
ResultSet resultSet = statement.executeQuery(
"SELECT Student.StudentID, Student.StudentName, Subject.SubjectCode, Subject.SubjectName, Marks.Mark " +
"FROM Student " +
"INNER JOIN Subject ON Student.SubjectCode = Subject.SubjectCode " +
"LEFT JOIN Marks ON Student.StudentID = Marks.StudentID AND Subject.SubjectCode = Marks.SubjectCode");
System.out.println("===== Marks of All Students in Every Subject =====");
System.out.println("StudentID\tStudentName\tSubjectCode\tSubjectName\tMark");
while (resultSet.next()) {
int studentID = resultSet.getInt("StudentID");
String studentName = resultSet.getString("StudentName");
int subjectCode = resultSet.getInt("SubjectCode");
String subjectName = resultSet.getString("SubjectName");
int mark = resultSet.getInt("Mark");
System.out.println(studentID + "\t\t" + studentName + "\t\t" + subjectCode + "\t\t" + subjectName + "\t\t" + mark);
}
} catch (SQLException e) {
e.printStackTrace();
}
}
}
