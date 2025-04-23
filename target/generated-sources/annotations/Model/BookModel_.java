package Model;

import Model.LoanModel;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-04-23T17:18:34")
@StaticMetamodel(BookModel.class)
public class BookModel_ { 

    public static volatile ListAttribute<BookModel, LoanModel> loan;
    public static volatile SingularAttribute<BookModel, Integer> yearPublication;
    public static volatile SingularAttribute<BookModel, String> gender;
    public static volatile SingularAttribute<BookModel, String> author;
    public static volatile SingularAttribute<BookModel, String> isbn;
    public static volatile SingularAttribute<BookModel, Boolean> available;
    public static volatile SingularAttribute<BookModel, Long> id;
    public static volatile SingularAttribute<BookModel, String> title;

}