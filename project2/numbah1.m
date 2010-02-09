function [runs] = numbah1
non_smiling = cell(36, 1);
smiling = cell(36, 1);

cd ~/Dropbox/school/project2
cd class_images/smiling_cropped;
files = dir('*.jpg');
for i = 1:36
  smiling{i} = rgb2gray(double(imread(files(i).name)) / 255);
  smiling{i} = smiling{i} / norm(smiling{i});
end
cd ../nonsmiling_cropped;
files = dir('*.jpg');
for i = 1:36
  non_smiling{i} = rgb2gray(double(imread(files(i).name)) / 255);
  non_smiling{i} = non_smiling{i} / norm(non_smiling{i});
end
cd ../..;

figure(1);
imagesc(smiling{1});
figure(2);
imagesc(non_smiling{1});
runs = cell(18, 1);
for i = 1:2:35
  [avgface eigfaces] = eigenfaces(non_smiling, i);
  
  user_coeffs = zeros(i, 36);
  for j = 1:36
    user_coeffs(:, j) = project_face(avgface, eigfaces, smiling{j});
  end
  
  mat = zeros(36);
  for j = 1:36
    order = recognize_face(avgface, eigfaces, user_coeffs, smiling{j});
    mat(j, :) = order;
  end
  runs{round(i / 2)} = mat;
end

end

