function [nonsmiling] = read_nonsmiling
cd ~/Dropbox/school/project2
C = cell(36, 1);
cd class_images/nonsmiling_cropped;
files = dir('*.jpg');
for i = 1:36
  C{i} = rgb2gray(double(imread(files(i).name)) / 255);
  C{i} = C{i} / norm(C{i});
end
nonsmiling = C
cd ../..
end