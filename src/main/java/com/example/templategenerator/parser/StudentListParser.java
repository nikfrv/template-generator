package com.example.templategenerator.parser;

import com.example.templategenerator.model.Student;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface StudentListParser {
    List<Student> parse(File excelFile) throws IOException;
}
