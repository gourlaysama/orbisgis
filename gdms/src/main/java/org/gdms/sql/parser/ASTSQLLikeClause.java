/* Generated By:JJTree: Do not edit this line. ASTSQLLikeClause.java */

package org.gdms.sql.parser;

public class ASTSQLLikeClause extends SimpleNode {
  public ASTSQLLikeClause(int id) {
    super(id);
  }

  public ASTSQLLikeClause(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
