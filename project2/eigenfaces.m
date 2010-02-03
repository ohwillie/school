function [avgface eigfaces] = eigenfaces(faces, k)
[faces_rsz m] = rsz2std(faces);
avgface = get_avgface(faces_rsz);
faces_std = cellfun(@(x) x - avgface, faces_rsz, 'UniformOutput', false);
faces_flt = cellfun(@(x) x(:)', faces_std, 'UniformOutput', false);
faces_mat = cell2mat(faces_flt);
my_cov = cov(faces_mat);
[u s v] = svds(my_cov, k);
eigfaces = reshape(u * s, [m k]);
end