package com.adobe.examples.htl.core.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceDecorator;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;

@Service(value = ResourceDecorator.class)
@Component
public class LayoutSwitcher implements ResourceDecorator {

	private static final String SLING_SCRIPT_FOLDER = "sling:scriptFolder";
	
	@Override
	public Resource decorate(Resource resource) {
		
		if ( resource instanceof LayoutResource) {
			return null;
		}
		
		String path = resource.getPath();
		
		if (path != null && path.startsWith("/content/")) {
			String scriptFolder = getSlingScriptFolder(resource);
			
			if (scriptFolder == null ) {
				return null;
			}

			String componentName = resource.getResourceType();
			if (componentName != null ) {
				componentName = StringUtils.substringAfterLast(componentName, "/");
			}
			
			if ( StringUtils.isNotBlank( scriptFolder) && StringUtils.isNotBlank(componentName)) {
				String newComponentName = scriptFolder + "/" + componentName;
				
				// check if there is a local version available, if so then use this		
				if ( resource.getResourceResolver().getResource(newComponentName) != null ) {
					LayoutResource lr = new LayoutResource(resource);
					// overriding the resource-type
					lr.setSlingResourceType(newComponentName);
					
					return lr;
				}
			} 
		}
		return null;
		
	}

	private String getSlingScriptFolder(Resource resource) {
		
		InheritanceValueMap ivp = new HierarchyNodeInheritanceValueMap(resource);
		
		return ivp.getInherited(SLING_SCRIPT_FOLDER, String.class);
	}

	@Override
	public Resource decorate(Resource resource, HttpServletRequest arg1) {
		return this.decorate(resource);
	}

}
