package br.com.lanche.services;

import br.com.lanche.models.Lanche;
import br.com.lanche.repositories.LancheRepositoryImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class LancheService {

    private static final String PASTA_IMAGENS = "produto_imagens";

    public LancheService() {
        File pasta = new File(PASTA_IMAGENS);
        if (!pasta.exists()) {
            pasta.mkdir();
        }
    }

    public void salvarImagem(Lanche lanche, String caminhoOrigem) throws IOException {
        String extensao = caminhoOrigem.substring(caminhoOrigem.lastIndexOf("."));
        File destino = new File(PASTA_IMAGENS + File.separator + lanche.getId() + extensao);

        Files.copy(new File(caminhoOrigem).toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

        lanche.setCaminhoImagem(destino.getAbsolutePath());
    }

    public void excluirImagem(Lanche lanche) {
        if (lanche.getCaminhoImagem() != null) {
            File arquivo = new File(lanche.getCaminhoImagem());
            if (arquivo.exists()) {
                arquivo.delete();
            }
        }
    }
}
