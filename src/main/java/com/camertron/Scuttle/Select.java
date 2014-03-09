package com.camertron.Scuttle;

import java.util.ArrayList;

public class Select {
  private ArrayList<SelectColumn> m_alColumns;

  public Select(ArrayList<SelectColumn> alColumns) {
    m_alColumns = alColumns;
  }

  public ArrayList<SelectColumn> getColumns() {
    return m_alColumns;
  }
}
