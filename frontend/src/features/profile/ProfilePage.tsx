import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { User, Mail, Lock, Shield, Building2, Save, Loader2, KeyRound } from 'lucide-react';
import { toast } from 'sonner';

import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';

import { authApi } from '@/api/authApi';
import { useAuthStore } from '@/store/useAuthStore';

const profileSchema = z.object({
  fullname: z.string().min(2, 'Tên phải có ít nhất 2 ký tự'),
  email: z.string().email('Email không hợp lệ'),
  password: z.string().optional().refine(val => !val || val.length >= 6, {
    message: 'Mật khẩu mới phải có ít nhất 6 ký tự',
  }),
});

type ProfileFormValues = z.infer<typeof profileSchema>;

export function ProfilePage() {
  const { employee, updateEmployee } = useAuthStore();
  const [isLoading, setIsLoading] = useState(false);
  const [isFetching, setIsFetching] = useState(true);
  
  const roleName = typeof employee?.role === 'object' ? employee?.role?.name : employee?.role;
  const deptName = typeof employee?.department === 'object' ? employee?.department?.name : employee?.department;

  const form = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      fullname: employee?.fullname || employee?.fullName || '',
      email: employee?.email || '',
      password: '',
    },
  });

  useEffect(() => {
    let mounted = true;
    const fetchProfile = async () => {
      try {
        const data = await authApi.getMe();
        if (mounted) {
          updateEmployee(data);
          form.reset({
            fullname: data.fullname || data.fullName || '',
            email: data.email || '',
            password: '',
          });
        }
      } catch (error: any) {
        if (mounted) toast.error(error.message || 'Lỗi khi tải thông tin cá nhân');
      } finally {
        if (mounted) setIsFetching(false);
      }
    };
    
    fetchProfile();
    return () => { mounted = false; };
  }, [form, updateEmployee]);

  const onSubmit = async (values: ProfileFormValues) => {
    const id = employee?.id || employee?.employeeId;
    if (!id) return;
    
    setIsLoading(true);
    try {
      const updateData: any = {
        fullname: values.fullname,
        email: values.email,
      };
      
      if (values.password) {
        updateData.password = values.password;
      }
      
      await authApi.updateProfile(id, updateData);
      toast.success('Cập nhật hồ sơ thành công!');
      
      form.setValue('password', '');
      const newData = await authApi.getMe();
      updateEmployee(newData);
    } catch (error: any) {
      toast.error(error.message || 'Cập nhật thất bại');
    } finally {
      setIsLoading(false);
    }
  };

  if (isFetching) {
    return (
      <div className="flex h-[50vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-red-600" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">Hồ sơ cá nhân</h2>
        <p className="text-slate-500">Quản lý thông tin tài khoản và bảo mật của bạn.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Left Column: Avatar & Static Info */}
        <div className="col-span-1 space-y-6">
          <Card className="border-slate-200 shadow-xs">
            <CardContent className="pt-8 flex flex-col items-center text-center">
              <div className="h-28 w-28 rounded-full bg-slate-100 flex items-center justify-center border-4 border-white shadow-md mb-4 overflow-hidden relative">
                <div className="absolute inset-0 bg-gradient-to-br from-red-500 to-rose-600 opacity-90" />
                <span className="relative text-4xl font-black text-white">
                  {(employee?.fullname || employee?.fullName || 'U').charAt(0).toUpperCase()}
                </span>
              </div>
              <h3 className="text-xl font-bold text-slate-900">{employee?.fullname || employee?.fullName}</h3>
              <p className="text-sm font-medium text-slate-500 mt-0.5">{employee?.employeeCode}</p>
              
              <div className="mt-8 w-full space-y-3">
                <div className="flex items-center gap-3 text-sm text-slate-700 bg-slate-50 p-3 rounded-lg border border-slate-100">
                  <Shield className="h-4 w-4 text-red-500 shrink-0" />
                  <span className="font-bold">{roleName}</span>
                </div>
                <div className="flex items-center gap-3 text-sm text-slate-700 bg-slate-50 p-3 rounded-lg border border-slate-100">
                  <Building2 className="h-4 w-4 text-slate-400 shrink-0" />
                  <span className="line-clamp-1 text-left font-medium">{deptName || 'Khối Cơ Quan'}</span>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column: Edit Form */}
        <div className="col-span-1 md:col-span-2">
          <Card className="border-slate-200 shadow-xs h-full flex flex-col">
            <CardHeader className="border-b border-slate-100 pb-5">
              <CardTitle className="text-lg">Thông tin tài khoản</CardTitle>
              <CardDescription>
                Cập nhật thông tin liên lạc và thay đổi mật khẩu đăng nhập.
              </CardDescription>
            </CardHeader>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="flex flex-col flex-1">
                <CardContent className="space-y-5 pt-6 flex-1">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <FormField
                      control={form.control}
                      name="fullname"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="text-slate-700 font-semibold">Họ và Tên</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <User className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                              <Input className="pl-9 bg-slate-50 border-slate-200 focus-visible:ring-red-500" placeholder="Nguyễn Văn A" {...field} />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="email"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="text-slate-700 font-semibold">Email liên hệ</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <Mail className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                              <Input className="pl-9 bg-slate-50 border-slate-200 focus-visible:ring-red-500" type="email" placeholder="email@viettel.com.vn" {...field} />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <div className="pt-6 pb-2 border-t border-slate-100 mt-6">
                    <h4 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                      <KeyRound className="h-4 w-4 text-red-500" />
                      Đổi mật khẩu (Tùy chọn)
                    </h4>
                    <p className="text-xs text-slate-500 mt-1">Bỏ trống trường này nếu bạn không muốn thay đổi mật khẩu hiện tại.</p>
                  </div>

                  <FormField
                    control={form.control}
                    name="password"
                    render={({ field }) => (
                      <FormItem>
                        <FormControl>
                          <div className="relative max-w-md">
                            <Lock className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
                            <Input 
                              className="pl-9 bg-slate-50 border-slate-200 focus-visible:ring-red-500" 
                              type="password" 
                              placeholder="Nhập mật khẩu mới..." 
                              {...field} 
                            />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </CardContent>
                <CardFooter className="flex justify-end border-t border-slate-100 pt-4 pb-4 bg-slate-50/50 rounded-b-xl">
                  <Button type="submit" disabled={isLoading || !form.formState.isDirty} className="bg-slate-900 hover:bg-slate-800 text-white gap-2 px-6">
                    {isLoading ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      <Save className="h-4 w-4" />
                    )}
                    Lưu cập nhật
                  </Button>
                </CardFooter>
              </form>
            </Form>
          </Card>
        </div>
      </div>
    </div>
  );
}
