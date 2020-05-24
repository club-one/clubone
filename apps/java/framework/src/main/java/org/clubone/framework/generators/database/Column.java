/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clubone.framework.generators.database;

import lombok.Data;

/**
 *
 * @author netuser
 */
@Data
public class Column {
    
    private String fieldName;
    
    private Class javaType;
    
    private String databaseTypeName;
    
}
