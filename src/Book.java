import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

// Annotation @XmlRootElement chỉ định phần tử gốc cho đối tượng này trong XML
@XmlRootElement(name = "book")
// Annotation @XmlType định nghĩa thứ tự các phần tử con trong XML
@XmlType(propOrder = {"isbn", "title", "author", "publicationYear", "publisher", "pages", "genre", "price"})
public class Book {
    private String title;
    private String author;
    private int publicationYear;
    private String publisher;
    private int pages;
    private String genre;
    private double price;
    private String isbn; // Mã sách (ISBN) - Thường là duy nhất

    // Constructor mặc định (cần thiết cho JAXB)
    public Book() {
    }

    // Constructor đầy đủ tham số
    public Book(String isbn, String title, String author, int publicationYear, String publisher, int pages, String genre, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.pages = pages;
        this.genre = genre;
        this.price = price;
    }

    // Getters và Setters
    // Annotation @XmlElement chỉ định tên của phần tử XML tương ứng
    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @XmlElement
    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    @XmlElement
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @XmlElement
    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @XmlElement
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @XmlElement
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @XmlElement
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", publisher='" + publisher + '\'' +
                ", pages=" + pages +
                ", genre='" + genre + '\'' +
                ", price=" + price +
                '}';
    }
}