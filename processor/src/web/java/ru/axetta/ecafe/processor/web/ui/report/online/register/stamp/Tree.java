/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online.register.stamp;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class Tree<T> implements Visitable<T> {
    // NB: LinkedHashSet preserves insertion order
    private final Set<Tree<T>> children = new LinkedHashSet<Tree<T>>();
    private final T data;
    private final int level;
    private final String fullName;

    public Tree(T data, int level, String fullName) {
        this.data = data;
        this.level = level;
        this.fullName = fullName;
    }

    public void accept(Visitor<T> visitor) {
        visitor.visitData(this, data);
        for (Tree<T> child : children) {
            Visitor<T> childVisitor = visitor.visitTree(child);
            child.accept(childVisitor);
        }
    }

    public Tree<T> child(T data, String allName) {
        for (Tree<T> child: children ) {
            if (child.data.equals(data)) {
                return child;
            }
        }
        return child(new Tree<T>(data, level+1, allName));
    }

    public Tree<T> child(Tree<T> child) {
        children.add(child);
        return child;
    }

    public Integer getChildCount(){
        return children.size();
    }

    public Integer getLevel() {
        return level;
    }

    public String getFullName() {
        return fullName;
    }
}
