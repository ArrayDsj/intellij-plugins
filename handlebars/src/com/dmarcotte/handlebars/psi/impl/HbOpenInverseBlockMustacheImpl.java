package com.dmarcotte.handlebars.psi.impl;

import com.dmarcotte.handlebars.psi.HbOpenInverseBlockMustache;
import com.intellij.lang.ASTNode;
import icons.HandlebarsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HbOpenInverseBlockMustacheImpl extends HbOpenBlockMustacheImpl implements HbOpenInverseBlockMustache {
  public HbOpenInverseBlockMustacheImpl(@NotNull ASTNode astNode) {
    super(astNode);
  }

  @Nullable
  @Override
  public Icon getIcon(int flags) {
    return HandlebarsIcons.Elements.OpenInverse;
  }
}
