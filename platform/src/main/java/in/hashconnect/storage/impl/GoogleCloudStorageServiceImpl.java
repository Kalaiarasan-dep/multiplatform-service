package in.hashconnect.storage.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.vo.FileContent;
import org.springframework.beans.factory.annotation.Autowired;

public class GoogleCloudStorageServiceImpl extends AbstractStorageService implements StorageService {

    @Autowired
    private  Storage storage;

    @Override
    public void put(FileContent fileContent) {
        Blob blob = storage.create(BlobInfo.newBuilder(fileContent.getBucket(),formatFileName(fileContent))
                .setContentType(fileContent.getContentType())
                .build(),fileContent.getData());
    }

    @Override
    public FileContent get(FileContent fileContent) {
        String fileName = this.formatFileName(fileContent);
        if (!this.exist(fileContent)) {
            return null;
        }
        Blob blob = storage.get(fileContent.getBucket(),fileName);
        fileContent.setData(blob.getContent());
        fileContent.setContentType(blob.getContentType());
        return fileContent;
    }

    @Override
    public boolean exist(FileContent fileContent) {
        String fileName = formatFileName(fileContent);
        Blob blob = storage.get(fileContent.getBucket(),fileName);
        return blob != null;
    }

    @Override
    public boolean delete(FileContent fileContent) {
        return storage.delete(fileContent.getBucket(),formatFileName(fileContent));
    }
}
