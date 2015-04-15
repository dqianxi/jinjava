/**********************************************************************
Copyright (c) 2014 HubSpot Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **********************************************************************/
package com.hubspot.jinjava.lib.tag;

import org.apache.commons.lang3.StringUtils;

import com.hubspot.jinjava.doc.annotations.JinjavaDoc;
import com.hubspot.jinjava.interpret.InterpretException;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.tree.Node;
import com.hubspot.jinjava.tree.TagNode;

/**
 * {% ifchange var %}
 * 
 * @author anysome
 * 
 */
@JinjavaDoc(value="outputs the tag contents if the given variable has changed since a prior invocation of this tag")
public class IfchangedTag implements Tag {

  private static final String LASTKEY = "'IF\"CHG";
  private static final String TAGNAME = "ifchanged";
  private static final String ENDTAGNAME = "endif";

  @Override
  public String interpret(TagNode tagNode, JinjavaInterpreter interpreter) {
    if (StringUtils.isBlank(tagNode.getHelpers())) {
      throw new InterpretException("Tag 'ifchanged' expects 1 helper >>> 0", tagNode.getLineNumber());
    }
    boolean isChanged = true;
    String var = tagNode.getHelpers().trim();
    Object older = interpreter.getContext().get(LASTKEY + var);
    Object test = interpreter.retraceVariable(var, tagNode.getLineNumber());
    if (older == null) {
      if (test == null) {
        isChanged = false;
      }
    } else if (older.equals(test)) {
      isChanged = false;
    }
    interpreter.getContext().put(LASTKEY + var, test);
    if (isChanged) {
      StringBuilder sb = new StringBuilder();
      for (Node node : tagNode.getChildren()) {
        sb.append(node.render(interpreter));
      }
      return sb.toString();
    }
    return "";
  }

  @Override
  public String getEndTagName() {
    return ENDTAGNAME;
  }

  @Override
  public String getName() {
    return TAGNAME;
  }

}
