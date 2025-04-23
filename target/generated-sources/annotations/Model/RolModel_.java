package Model;

import Model.PermissionModel;
import Model.UserModel;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-04-23T17:18:34")
@StaticMetamodel(RolModel.class)
public class RolModel_ { 

    public static volatile SingularAttribute<RolModel, String> name;
    public static volatile ListAttribute<RolModel, PermissionModel> permission;
    public static volatile SingularAttribute<RolModel, Long> id;
    public static volatile ListAttribute<RolModel, UserModel> user;

}