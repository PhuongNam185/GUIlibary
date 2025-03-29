import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

// Lớp này đóng gói danh sách các cuốn sách, giúp JAXB dễ dàng tạo cấu trúc XML <library><book>...</book><book>...</book></library>
@XmlRootElement(name = "library")
public class Library {

    private List<Book> books;

    public Library() {
        books = new ArrayList<>();
    }

    @XmlElement(name = "book") // Chỉ định tên phần tử cho mỗi cuốn sách trong danh sách
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
}