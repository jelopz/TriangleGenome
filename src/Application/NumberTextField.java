package Application;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField
{
  public NumberTextField()
  {
    this.setPromptText("Enter only #s");
  }

  @Override
  public void replaceText(int i, int i1, String string)
  {
    if (string.matches("[0-9]"))
    {
      super.replaceText(i, i1, string);
    }
    else if (string.isEmpty())
    {
      super.replaceText(0, 0, "0");
    }
  }

  @Override
  public void replaceSelection(String string)
  {
    super.replaceSelection(string);
  }
}