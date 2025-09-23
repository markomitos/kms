package uns.ftn.kms.services;

import org.springframework.stereotype.Service;
import uns.ftn.kms.dtos.AlgorithmResponse;
import uns.ftn.kms.models.alghoritms.AsymmetricAlgorithm;
import uns.ftn.kms.models.alghoritms.SymmetricAlgorithm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlgorithmService {

    public List<AlgorithmResponse> getSymmetricAlgorithms() {
        return Arrays.stream(SymmetricAlgorithm.values())
                .map(algo -> new AlgorithmResponse(algo.name(), "Symmetric Algorithm: " + algo.getJavaName()))
                .collect(Collectors.toList());
    }

    public List<AlgorithmResponse> getAsymmetricAlgorithms() {
        return Arrays.stream(AsymmetricAlgorithm.values())
                .map(algo -> new AlgorithmResponse(algo.name(), "Asymmetric Algorithm: " + algo.getJavaName()))
                .collect(Collectors.toList());
    }
}