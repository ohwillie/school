function [smiling] = read_smiling
cd ~/Dropbox/school/project2
C = cell(36, 1);
cd class_images/smiling_cropped;
files = dir('*.jpg');
for i = 1:36
  C{i} = rgb2gray(double(imread(files(i).name)) / 255);
  C{i} = C{i} / norm(C{i});
end
smiling = C
cd ../..
end