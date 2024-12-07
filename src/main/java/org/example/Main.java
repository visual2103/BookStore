package org.example;

        import org.example.database.DataBaseConnectionFactory;
        import org.example.model.Book;
        import org.example.model.Sales;
        import org.example.model.builder.BookBuilder;
        import org.example.model.builder.SalesBuilder;
        import org.example.repository.book.BookRepository;
        import org.example.repository.book.BookRepositoryCacheDecorator;
        import org.example.repository.book.BookRepositoryMySQL;
        import org.example.repository.book.Cache;
        import org.example.repository.sales.SalesRepository;
        import org.example.repository.sales.SalesRepositoryMySQL;
        import org.example.service.book.BookService;
        import org.example.service.book.BookServiceImplementation;
        import org.example.service.sales.SalesService;
        import org.example.service.sales.SalesServiceImplementation;

        import java.sql.Connection;
        import java.time.LocalDate;
        import java.util.List;


public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        Book book = new BookBuilder()
                .setTitle("Ion")
                .setAuthor("Liviu Rebreanu")
                .setPublishedDate(LocalDate.of(1920 , 7,20))
                .build() ;
//        System.out.println(book);
//
//        BookRepository bookRepository = new BookRepositoryMock();
//        bookRepository.save(new BookBuilder().setTitle("Mara").setTitle("Ioan Slavici").setPublishedDate(LocalDate.of(1930,6,3)).build()) ;
//        System.out.println(bookRepository.findAll());

        Connection connection = DataBaseConnectionFactory.getConnectionWrapper(false).getConnection() ;
        BookRepository bookRepository = new BookRepositoryCacheDecorator(new BookRepositoryMySQL(connection) , new Cache<>()) ;

        BookService bookService = new BookServiceImplementation(bookRepository) ;

        bookService.save(book) ;
        System.out.println(bookService.findAll());
        Book book1 = new BookBuilder().setTitle("Mara").setAuthor("Ioan Slavici").setPublishedDate(LocalDate.of(1930,6,3)).build() ;
        bookService.save(book1);
        System.out.println(bookRepository.findAll());
        bookService.delete(book);
        bookService.save(book);
        System.out.println(bookService.findAll());

        SalesRepository salesRepository = new SalesRepositoryMySQL(connection);


        Sales sale1 = new SalesBuilder()
                .setBookTitle("Titlu Carte 1")
                .setBookAuthor("Autor 1")
                .setQuantity(3)
                .setTotalPrice(59.97f)
                .setSaleDate(LocalDate.of(2023, 10, 1))
                .build();

        Sales sale2 = new SalesBuilder()
                .setBookTitle("Titlu Carte 2")
                .setBookAuthor("Autor 2")
                .setQuantity(2)
                .setTotalPrice(39.98f)
                .setSaleDate(LocalDate.of(2023, 10, 2))
                .build();

        Sales sale3 = new SalesBuilder()
                .setBookTitle("Titlu Carte 3")
                .setBookAuthor("Autor 3")
                .setQuantity(5)
                .setTotalPrice(99.95f)
                .setSaleDate(LocalDate.of(2023, 10, 3))
                .build();


        salesRepository.save(sale1);
        salesRepository.save(sale2);
        salesRepository.save(sale3);

        SalesService salesService = new SalesServiceImplementation(salesRepository) ;
        List<Sales> salesList = salesService.getAllSales();

        for (Sales sale : salesList) {
            System.out.println("ID: " + sale.getId());
            System.out.println("Titlu: " + sale.getBookTitle());
            System.out.println("Autor: " + sale.getBookAuthor());
            System.out.println("Cantitate: " + sale.getQuantity());
            System.out.println("Preț Total: " + sale.getTotalPrice());
            System.out.println("Data Vânzării: " + sale.getSaleDate());
            System.out.println("-----------------------------");
        }


    }
}