/* Generated By:JJTree: Do not edit this line. ASTSQLTableRef.java */

package org.gdms.sql.parser;

public class ASTSQLTableRef extends SimpleNode {
  public ASTSQLTableRef(int id) {
    super(id);
  }

  public ASTSQLTableRef(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
