package com.example.library.services;

import com.example.library.models.Variable;
import com.example.library.repositories.VariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VariableService {

    @Autowired
    private VariableRepository variableRepository;

    public String getVariableValue(String name, String defaultValue) {
        return variableRepository.findByName(name)
                .map(Variable::getValue)
                .orElse(defaultValue);
    }

    public Long getVariableValueAsLong(String name, Long defaultValue) {
        return variableRepository.findByName(name)
                .map(var -> {
                    try {
                        return Long.parseLong(var.getValue());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    public void setVariableValue(String name, String value) {
        Optional<Variable> existingVariable = variableRepository.findByName(name);

        if (existingVariable.isPresent()) {
            Variable variable = existingVariable.get();
            variable.setValue(value);
            variableRepository.save(variable);
        } else {
            Variable newVariable = new Variable(name, value);
            variableRepository.save(newVariable);
        }
    }

    public void deleteVariable(String name) {
        variableRepository.findByName(name)
                .ifPresent(variableRepository::delete);
    }

    public List<Variable> getAllVariables() {
        return variableRepository.findAll();
    }
}
